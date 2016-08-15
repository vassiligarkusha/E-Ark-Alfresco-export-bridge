package dk.magenta.eark.erms.repository;

import dk.magenta.eark.erms.ErmsBaseTypes;
import dk.magenta.eark.erms.Utils;
import dk.magenta.eark.erms.exceptions.ErmsIOException;
import dk.magenta.eark.erms.exceptions.ErmsNotSupportedException;
import dk.magenta.eark.erms.exceptions.ErmsRuntimeException;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.spi.*;

import javax.json.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lanre.
 * @author Andreas Kring <andreas@magenta.dk>
 */
public class CmisSessionWorkerImpl implements CmisSessionWorker {
    private Session session;
    private ObjectFactory objectFactory;
    private OperationContext operationContext;
    private TreeNode<CmisNode> treeNode;

    /**
     * Constructor
     *
     * @param session
     */
    public CmisSessionWorkerImpl(Session session) {
        this.session = session;
        this.objectFactory = session.getObjectFactory();
        this.operationContext = session.createOperationContext();
        //DELTA HACK
        this.session.getDefaultContext().setIncludeAllowableActions(false);
        this.operationContext.setIncludeAllowableActions(false);
        this.session.setDefaultContext(this.operationContext);
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
        ObjectData parent_ = getNavigationService().getFolderParent(this.session.getRepositoryInfo().getId(),folderObjectId, null, null);
        return this.objectFactory.convertObject(parent_, this.operationContext);
    }

    /**
     * returns the properties of a folder and its children
     *
     * @return Json object that represents the folder
     */
    @Override
    public JsonObject getFolder(String folderObjectId) {
        RepositoryCapabilities caps = this.session.getRepositoryInfo().getCapabilities();
        JsonObjectBuilder folderBuilder = Json.createObjectBuilder();
        if (!caps.isGetDescendantsSupported())
            throw new ErmsNotSupportedException("The operation requested is not supported by the repository");

        try {
            Folder folder = (Folder) this.session.getObject(folderObjectId);
            //Just to be sure
            if (!folder.getId().equalsIgnoreCase(folderObjectId))
                throw new ErmsRuntimeException("Folder Id mismatch. Please contact system administrator to resolve the");
            List<CmisObject> children = this.getFolderChildren(folder.getId());
            List<JsonObject> jsonRep = children.stream().map(this::extractUsefulProperties).collect(Collectors.toList());
            JsonArrayBuilder cb = Json.createArrayBuilder();
            JsonObject tmp = this.extractUsefulProperties(folder);
            jsonRep.forEach(cb::add);
            folderBuilder.add("properties", tmp);
            folderBuilder.add("children", cb.build());

        } catch (Exception ge) {
            throw new ErmsIOException("Unable to read folder items for root folder:\n" + ge.getMessage());
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

    /**
     * Just to filter out the useful properties for UI consumption.
     * A CMIS capable repository can return too much metadata about an object, until we fix the mapping feature, we use
     * this to filter out useful generic properties for the UI
     * @param cmisObject
     * @return
     */
    private JsonObject extractUsefulProperties(CmisObject cmisObject){
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

        switch (cmisObject.getBaseTypeId().value()){
            case "cmis:document" :
                Document doc = (Document) cmisObject ;
                jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "document");
                jsonBuilder.add(ErmsBaseTypes.LAST_MOD_DATE, Utils.convertToISO8601Date(cmisObject.getLastModificationDate()));
                jsonBuilder.add(ErmsBaseTypes.CONTENT_SIZE, doc.getContentStreamLength());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_LENGTH, doc.getContentStreamLength());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_MIMETYPE, doc.getContentStreamMimeType());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_ID, doc.getContentStreamId());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_FILENAME, doc.getContentStreamFileName());
                jsonBuilder.add(ErmsBaseTypes.PATH, doc.getPaths().get(0));
                break;
            case "cmis:folder":
                Folder folder = (Folder) cmisObject ;
                jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "folder");
                jsonBuilder.add(ErmsBaseTypes.PATH, folder.getPath());
                break;
            default: Utils.getPropertyPostFixValue(cmisObject.getBaseTypeId().value());
                break;
        }
        jsonBuilder.add(ErmsBaseTypes.OBJECT_ID, cmisObject.getId() );
        jsonBuilder.add(ErmsBaseTypes.OBJECT_TYPE_ID, cmisObject.getType().getId());
        jsonBuilder.add(ErmsBaseTypes.NAME, cmisObject.getName() );
        jsonBuilder.add(ErmsBaseTypes.CREATION_DATE, Utils.convertToISO8601Date(cmisObject.getCreationDate()) );
        jsonBuilder.add(ErmsBaseTypes.CREATED_BY, cmisObject.getCreatedBy() );
        jsonBuilder.add(ErmsBaseTypes.LAST_MODIFIED, cmisObject.getLastModifiedBy() );

        return jsonBuilder.build();
    }

    @Override
    public void addNode(Object node) {
    	// TODO Auto-generated method stub
    	
    }

}
