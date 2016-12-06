package dk.magenta.eark.erms.repository;

import dk.magenta.eark.erms.ErmsBaseTypes;
import dk.magenta.eark.erms.Utils;
import dk.magenta.eark.erms.db.DatabaseConnectionStrategy;
import dk.magenta.eark.erms.db.JDBCConnectionStrategy;
import dk.magenta.eark.erms.ead.MappingParser;
import dk.magenta.eark.erms.exceptions.ErmsDatabaseException;
import dk.magenta.eark.erms.exceptions.ErmsIOException;
import dk.magenta.eark.erms.exceptions.ErmsNotSupportedException;
import dk.magenta.eark.erms.repository.profiles.Profile;
import dk.magenta.eark.erms.system.PropertiesHandlerImpl;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lanre.
 */
public class CmisSessionWorkerImpl implements CmisSessionWorker {
    private final Logger logger = LoggerFactory.getLogger(CmisSessionWorkerImpl.class);
    private Session session;
    private ObjectFactory objectFactory;
    private OperationContext operationContext;

    private Set<String> viewTypes;
    private Set<String> exportableTypes;

    /**
     *
     * @param connectionProfile the connection profile which contains the wsdl location and the auth details for the user
     * @param mapName the mapping for which we wish to apply for the session
     */
    public CmisSessionWorkerImpl(String connectionProfile, String mapName) {

        try {
            DatabaseConnectionStrategy dbConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl("settings.properties"));
            Cmis1Connector cmis1Connector = new Cmis1Connector();
            Profile connProfile = dbConnectionStrategy.getProfile(connectionProfile);
            //Get a CMIS session object
            this.session = cmis1Connector.getSession(connProfile);
            this.operationContext = this.session.createOperationContext();
            this.session.setDefaultContext(this.operationContext);
            this.objectFactory = session.getObjectFactory();
            //DELTA HACK
            this.session.getDefaultContext().setIncludeAllowableActions(false);
            this.operationContext.setIncludeAllowableActions(false);
            //initialise the export and view types
            initExportViewTypes(mapName);
        }
        catch (SQLException sqe) {
            sqe.printStackTrace();
            throw new ErmsDatabaseException("Unable to establish a session with the repository due to an issue with the " +
                    "db: "+sqe.getMessage());
        }
    }


    //<editor-fold desc="Webservices endpoints">

    /**
     * Returns the Navigation service
     *
     * @return
     */
    @Override
    public NavigationService getNavigationService() {
        return this.session.getBinding().getNavigationService();
    }

    /**
     * Returns the Repository service
     *
     * @return
     */
    @Override
    public RepositoryService getRepositoryService() {
        return this.session.getBinding().getRepositoryService();
    }

    /**
     * Returns the Versioning service
     *
     * @return
     */
    @Override
    public VersioningService getVersioningService() {
        return this.session.getBinding().getVersioningService();
    }

    /**
     * Returns the AclService service
     *
     * @return
     */
    @Override
    public AclService getACLService() {
        return this.session.getBinding().getAclService();
    }

    /**
     * Returns the Relationship service
     *
     * @return
     */
    @Override
    public RelationshipService getRelationshipService() {
        return this.session.getBinding().getRelationshipService();
    }

    /**
     * Returns the Policy service
     *
     * @return
     */
    @Override
    public PolicyService getPolicyService() {
        return this.session.getBinding().getPolicyService();
    }

    /**
     * Returns the Object service
     *
     * @return
     */
    @Override
    public ObjectService getObjectService() {
        return this.session.getBinding().getObjectService();
    }

    /**
     * Returns the Discovery service
     *
     * @return
     */
    @Override
    public DiscoveryService getDiscoveryService() {
        return this.session.getBinding().getDiscoveryService();
    }

    /**
     * Returns the MultiFiling service
     *
     * @return
     */
    @Override
    public MultiFilingService getMultiFilingService() {
        return this.session.getBinding().getMultiFilingService();
    }
    //</editor-fold>

    /**
     * Returns the properties and, optionally, the content stream of a document
     *
     * @param documentObjectId     the document objectId
     * @param includeContentStream boolean value which specifies whether to return the content stream of the document
     * @return
     */
    @Override
    public JsonObject getDocument(String documentObjectId, boolean includeContentStream) {
        JsonObjectBuilder documentBuilder = Json.createObjectBuilder();
        try {
            Document document = (Document) this.session.getObject(documentObjectId);
            JsonObject tmp = this.extractUsefulProperties(document);
            documentBuilder.add("properties", tmp);
            if (includeContentStream) {
                documentBuilder.add("contentStream", IOUtils.readAllLines(document.getContentStream().getStream()));
            }
        } catch (Exception ge) {
            System.out.println("********** Stacktrace **********\n");
            ge.printStackTrace();
            throw new ErmsIOException("\nUnable to read document:\n" + ge.getMessage());
        }
        return documentBuilder.build();
    }

    /**
     * Returns a list of CmisObject representing the children of a folder given it's cmis object id
     *
     * @return
     */
    @Override
    public List<CmisObject> getFolderChildren(String folderObjectId) {
        List<ObjectInFolderData> children = getNavigationService().getChildren(this.session.getRepositoryInfo().getId(), folderObjectId,
                null, null, false, IncludeRelationships.BOTH, null, false, null, null, null).getObjects();
        return children.stream().map(t -> objectFactory.convertObject(t.getObject(), this.operationContext)).collect(Collectors.toList());
    }

    /**
     * Returns the parent folder of a folder object
     *
     * @param folderObjectId the id of the folder
     * @return
     */
    @Override
    public CmisObject getFolderParent(String folderObjectId) {
        ObjectData parent_ = getNavigationService().getFolderParent(this.session.getRepositoryInfo().getId(), folderObjectId, null, null);
        return this.objectFactory.convertObject(parent_, this.operationContext);
    }

    /**
     * Returns a filtered view of the folder by restricting the returned types to the list of types defined in the mapping file.
     * @param folderObjectId The object id of the folder we're interested in
     * @return Json object that represents the folder
     */
    @Override
    public JsonObject getFolder(String folderObjectId) {
        JsonObjectBuilder folderBuilder = Json.createObjectBuilder();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Folder folder = (Folder) this.session.getObject(folderObjectId);
            List<CmisObject> children = new ArrayList<>();

            //Thread d1 search for series
            executorService.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.info("Running thread for folders with name: " + threadName);
                String queryStatement = "SELECT cmis:objectTypeId, cmis:objectId FROM cmis:series WHERE IN_FOLDER ('" + folderObjectId + "')";
                ItemIterable<QueryResult> q = this.session.query(queryStatement, false);
                //TODO refactor. This is an adapted copy of getFolder hacked to marshal the resulting query in children listqq
                for (QueryResult qr : q) {
                    String typeData = qr.getPropertyById("cmis:objectTypeId").getFirstValue().toString();
                    if (viewTypes.contains(typeData)) {
                        String objectId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                        CmisObject obj = this.session.getObject(this.session.createObjectId(objectId));
                        children.add(obj);
                    }
                }
            });
            
          //Thread d2 search for series
            executorService.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.info("Running thread for folders with name: " + threadName);
                String queryStatement = "SELECT cmis:objectTypeId, cmis:objectId FROM cmis:volume WHERE IN_FOLDER ('" + folderObjectId + "')";
                ItemIterable<QueryResult> q = this.session.query(queryStatement, false);
                //TODO refactor. This is an adapted copy of getFolder hacked to marshal the resulting query in children listqq
                for (QueryResult qr : q) {
                    String typeData = qr.getPropertyById("cmis:objectTypeId").getFirstValue().toString();
                    if (viewTypes.contains(typeData)) {
                        String objectId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                        CmisObject obj = this.session.getObject(this.session.createObjectId(objectId));
                        children.add(obj);
                    }
                }
            });
            
          //Thread d3 search for series
            executorService.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.info("Running thread for folders with name: " + threadName);
                String queryStatement = "SELECT cmis:objectTypeId, cmis:objectId FROM cmis:casefile WHERE IN_FOLDER ('" + folderObjectId + "')";
                ItemIterable<QueryResult> q = this.session.query(queryStatement, false);
                //TODO refactor. This is an adapted copy of getFolder hacked to marshal the resulting query in children listqq
                for (QueryResult qr : q) {
                    String typeData = qr.getPropertyById("cmis:objectTypeId").getFirstValue().toString();
                    if (viewTypes.contains(typeData)) {
                        String objectId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                        CmisObject obj = this.session.getObject(this.session.createObjectId(objectId));
                        children.add(obj);
                    }
                }
            });
            
          //Thread d4 search for series
            executorService.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.info("Running thread for folders with name: " + threadName);
                String queryStatement = "SELECT cmis:objectTypeId, cmis:objectId FROM cmis:case WHERE IN_FOLDER ('" + folderObjectId + "')";
                ItemIterable<QueryResult> q = this.session.query(queryStatement, false);
                //TODO refactor. This is an adapted copy of getFolder hacked to marshal the resulting query in children listqq
                for (QueryResult qr : q) {
                    String typeData = qr.getPropertyById("cmis:objectTypeId").getFirstValue().toString();
                    if (viewTypes.contains(typeData)) {
                        String objectId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                        CmisObject obj = this.session.getObject(this.session.createObjectId(objectId));
                        children.add(obj);
                    }
                }
            });
            
            //Thread 1 search for folders
            executorService.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.info("Running thread for folders with name: " + threadName);
                String queryStatement = "SELECT cmis:objectTypeId, cmis:objectId FROM cmis:folder WHERE IN_FOLDER ('" + folderObjectId + "')";
                ItemIterable<QueryResult> q = this.session.query(queryStatement, false);
                //TODO refactor. This is an adapted copy of getFolder hacked to marshal the resulting query in children listqq
                for (QueryResult qr : q) {
                    String typeData = qr.getPropertyById("cmis:objectTypeId").getFirstValue().toString();
                    if (viewTypes.contains(typeData)) {
                        String objectId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                        CmisObject obj = this.session.getObject(this.session.createObjectId(objectId));
                        children.add(obj);
                    }
                }
            });
            //Thread 2 search for documents next
            executorService.submit(() -> {
                String threadName = Thread.currentThread().getName();
                logger.info("Running thread for documents with name: " + threadName);
                String queryStatement = "SELECT cmis:objectTypeId, cmis:objectId FROM cmis:document " +
                        "WHERE IN_FOLDER ('" + folderObjectId + "')";
                ItemIterable<QueryResult> q = this.session.query(queryStatement, false);
                //TODO refactor. This is an adapted copy of getFolder hacked to marshal the resulting query in children listqq
                for (QueryResult qr : q) {
                    String typeData = qr.getPropertyById("cmis:objectTypeId").getFirstValue().toString();
                    if (viewTypes.contains(typeData)) {
                        String objectId = qr.getPropertyById("cmis:objectId").getFirstValue().toString();
                        CmisObject obj = this.session.getObject(this.session.createObjectId(objectId));
                        children.add(obj);
                    }
                }
            });

            executorService.shutdown();
            //Wait a reasonable amount of time for thread to finish
            executorService.awaitTermination(60, TimeUnit.SECONDS);

            List<JsonObject> jsonRep = children.stream().map(this::extractUsefulProperties).collect(Collectors.toList());
            JsonArrayBuilder cb = Json.createArrayBuilder();
            JsonObject tmp = this.extractUsefulProperties(folder);
            jsonRep.forEach(cb::add);
            folderBuilder.add("properties", tmp);
            folderBuilder.add("children", cb.build());
        } catch (InterruptedException e) {
            System.err.println("Some threads took too long to complete when listing folder items");
            logger.warn("=====> WARNING: prematurely terminated thread when attempting to list folder objects for " +
                    "folder: " + folderObjectId);
        } finally {
            executorService.shutdownNow();
        }
        return folderBuilder.build();
    }

    /**
     * returns the root folder
     *
     * @return
     */
    @Override
    public JsonObject getRootFolder() {
        RepositoryCapabilities caps = this.session.getRepositoryInfo().getCapabilities();
        JsonObjectBuilder rootFolder = Json.createObjectBuilder();
        if (!caps.isGetDescendantsSupported())
            throw new ErmsNotSupportedException("The operation requested is not supported by the repository");

        try {
            String rootFolderId = this.session.getRepositoryInfo().getRootFolderId();
            Folder root = (Folder) this.session.getObject(rootFolderId);
            List<CmisObject> children = this.getFolderChildren(root.getId());
            List<JsonObject> jsonRep = children.stream().map(this::extractUsefulProperties).collect(Collectors.toList());
            JsonArrayBuilder cb = Json.createArrayBuilder();
            JsonObject tmp = this.extractUsefulProperties(root);
            jsonRep.forEach(cb::add);
            rootFolder.add("properties", tmp);
            rootFolder.add("children", cb.build());

        } catch (Exception ge) {
            System.out.println("******** Error ********\n");
            ge.printStackTrace();
            System.out.println("\n******** End ********\n");
            throw new ErmsIOException("Unable to read folder items for root folder:\n" + ge.getMessage());
        }
        return rootFolder.build();
    }

    /**
     * Self explanatory
     *
     * @return
     */
    @Override
    public JsonObject getRepositoryInfo() {
        RepositoryInfo repositoryInfo = this.session.getRepositoryInfo();
        InputStream tmp = new ByteArrayInputStream(JSONConverter.convert(repositoryInfo, null, null, true)
                .toString().getBytes(StandardCharsets.UTF_8));
        JsonReader rdr = Json.createReader(tmp);
        return rdr.readObject();
    }

    @Override
    public Session getSession() {
        return session;
    }

    /**
     * Just to filter out the useful properties for UI consumption.
     * A CMIS capable repository can return too much metadata about an object, until we fix the mapping feature, we use
     * this to filter out useful generic properties for the UI
     *
     * @param cmisObject
     * @return
     */
    private JsonObject extractUsefulProperties(CmisObject cmisObject) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        String objTypeId = cmisObject.getPropertyValue("cmis:objectTypeId");

        switch (cmisObject.getBaseTypeId().value()) {
            case "cmis:document":
                Document doc = (Document) cmisObject;
                jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "document");
                jsonBuilder.add(ErmsBaseTypes.LAST_MOD_DATE, Utils.convertToISO8601Date(cmisObject.getLastModificationDate()));
                jsonBuilder.add(ErmsBaseTypes.CONTENT_SIZE, doc.getContentStreamLength());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_LENGTH, doc.getContentStreamLength());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_MIMETYPE, doc.getContentStreamMimeType());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_ID, doc.getContentStreamId());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_FILENAME, doc.getContentStreamFileName());
                //if (doc.getPaths() != null) {
                //	jsonBuilder.add(ErmsBaseTypes.PATH, doc.getPaths().get(0));
                //}
                jsonBuilder.add(ErmsBaseTypes.NAME, cmisObject.getName());
                jsonBuilder.add(ErmsBaseTypes.CREATION_DATE, Utils.convertToISO8601Date(cmisObject.getCreationDate()));
                jsonBuilder.add(ErmsBaseTypes.CREATED_BY, cmisObject.getCreatedBy());
                jsonBuilder.add(ErmsBaseTypes.LAST_MODIFIED, cmisObject.getLastModifiedBy());
                break;
            case "cmis:folder":
                Folder folder = (Folder) cmisObject;
                jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "folder");
                jsonBuilder.add(ErmsBaseTypes.PATH, folder.getPath());
                String deltaDocName = (cmisObject.getProperty("cmis:deltaDocName") != null)?cmisObject.getProperty("cmis:deltaDocName").getValueAsString():null;
                jsonBuilder.add(ErmsBaseTypes.NAME, (deltaDocName != null)?deltaDocName:cmisObject.getName());
                jsonBuilder.add(ErmsBaseTypes.CREATION_DATE, Utils.convertToISO8601Date(cmisObject.getCreationDate()));
                jsonBuilder.add(ErmsBaseTypes.CREATED_BY, cmisObject.getCreatedBy());
                jsonBuilder.add(ErmsBaseTypes.LAST_MODIFIED, cmisObject.getLastModifiedBy());
                break;
            case "cmis:function":
            	jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "folder");
                jsonBuilder.add(ErmsBaseTypes.NAME, cmisObject.getProperty("cmis:functionTitle").getValueAsString());
                break;
            case "cmis:series":
            	jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "folder");
                jsonBuilder.add(ErmsBaseTypes.NAME, cmisObject.getProperty("cmis:seriesTitle").getValueAsString());
                break;
            case "cmis:volume":
            	jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "folder");
                jsonBuilder.add(ErmsBaseTypes.NAME, cmisObject.getProperty("cmis:volumeTitle").getValueAsString());
                break;
            case "cmis:casefile":
            	jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "folder");
                jsonBuilder.add(ErmsBaseTypes.NAME, cmisObject.getProperty("cmis:caseFileTitle").getValueAsString());
                break;
            case "cmis:case":
            	jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "folder");
                jsonBuilder.add(ErmsBaseTypes.NAME, cmisObject.getProperty("cmis:caseTitle").getValueAsString());
                break;
            default:
                Utils.getPropertyPostFixValue(cmisObject.getBaseTypeId().value());
                break;
        }
        jsonBuilder.add("exportable", this.exportableTypes.contains(objTypeId));
        jsonBuilder.add(ErmsBaseTypes.OBJECT_ID, cmisObject.getId());
        jsonBuilder.add(ErmsBaseTypes.OBJECT_TYPE_ID, cmisObject.getType().getId());
        

        return jsonBuilder.build();
    }

    private void initExportViewTypes(String mapName){
        MappingParser mappingParser = new MappingParser(mapName);
        if(!mappingParser.getViewTypes().isEmpty() )
            this.viewTypes = new HashSet<>(mappingParser.getViewTypes());
        else
            this.viewTypes = new HashSet<>(mappingParser.getObjectTypeFromMap());

        this.exportableTypes = new HashSet<>(mappingParser.getObjectTypeFromMap());
    }
}
