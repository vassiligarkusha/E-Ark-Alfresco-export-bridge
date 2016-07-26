package dk.magenta.eark.erms;

import dk.magenta.eark.erms.exceptions.*;
import dk.magenta.eark.erms.repository.CmisSessionWorker;
import dk.magenta.eark.erms.repository.CmisSessionWorkerImpl;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Cmis1Connector {

    private final Logger logger = LoggerFactory.getLogger(Cmis1Connector.class);

    private static Map<String, Session> connections = new ConcurrentHashMap<String, Session>();

    /**
     * Get an Open CMIS session to use when talking to the CMIS repo.
     * Will check if there is already a connection to the CMIS repo
     * and re-use that session.
     *
     * @param connectionProfile the connection profile containing all info required to establish the connection
     * @return an Open CMIS Session object
     */
    public Session getSession(Profile connectionProfile) {
        Session session = connections.get(connectionProfile.getName());
        URL wsdlUrl;
        if (session == null) {
            //Validate that the url contains the wsdl we require or else throw an exception
            try {
                wsdlUrl = new URL(connectionProfile.getUrl());
                if (!isValidWebServicesUrls(wsdlUrl))
                    throw new ErmsURLException("No wsdl document or no service endpoints in " +
                            "document retrieved from: " + connectionProfile.getUrl());
            } catch (Exception ge) {
                logger.error("There's a problem with the url format of the connection profile. " +
                        "\nSee http://microformats.org/wiki/url-formats#HTTP or " +
                        "https://www.w3.org/Protocols/HTTP/HTTP2.html" +
                        "to make sure it conforms to the standards.\n");
                throw new ErmsURLException(ge.getMessage());
            }

            // No connection to Alfresco available, create a new one
            SessionFactory sessionFactory = SessionFactoryImpl.newInstance();

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put(SessionParameter.USER, connectionProfile.getUserName());
            parameters.put(SessionParameter.PASSWORD, connectionProfile.getPassword());
            parameters.put(SessionParameter.BINDING_TYPE, BindingType.WEBSERVICES.value());
            parameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE, wsdlUrl.toString());
            parameters.put(SessionParameter.COMPRESSION, "true");
            parameters.put(SessionParameter.CACHE_TTL_OBJECTS, "0"); // Caching is turned off

            // If there is only one repository exposed (e.g. Alfresco), these
            // lines will help detect it and its ID
            Repository alfrescoRepository = null;
            try {
                List<Repository> repositories = sessionFactory.getRepositories(parameters);
                if (repositories != null && repositories.size() > 0) {
                    logger.info("Found (" + repositories.size() + ") Alfresco repositories");
                    alfrescoRepository = repositories.get(0);
                    logger.info("Info about the first Alfresco repo [ID=" + alfrescoRepository.getId() +
                            "][name=" + alfrescoRepository.getName() +
                            "][CMIS ver supported=" + alfrescoRepository.getCmisVersionSupported() + "]");
                } else {
                    throw new CmisConnectionException("Could not connect to the Alfresco Server, no repository found!");
                }


                // Create a new session with the Alfresco repository
                session = alfrescoRepository.createSession();

                // Save connection for reuse
                connections.put(connectionProfile.getName(), session);
            } catch (Exception ge) {
                throw new ErmsConnectionException("There is an issue connecting to the repository:\n\n"
                        + ge.getMessage());
            }

        } else {
            logger.info("Already connected to Alfresco with the connection id (" + connectionProfile.getName() + ")");
        }

        return session;
    }

    public void listRepoCapabilities(RepositoryInfo repositoryInfo) {
        RepositoryCapabilities repoCapabilities = repositoryInfo.getCapabilities();

        System.out.println("aclCapability = " + repoCapabilities.getAclCapability().name());
        System.out.println("changesCapability = " + repoCapabilities.getChangesCapability().name());
        System.out.println("contentStreamUpdatable = " + repoCapabilities.getContentStreamUpdatesCapability().name());
        System.out.println("joinCapability = " + repoCapabilities.getJoinCapability().name());
        System.out.println("queryCapability = " + repoCapabilities.getQueryCapability().name());
        System.out.println("renditionCapability = " + repoCapabilities.getRenditionsCapability().name());
        System.out.println("allVersionsSearchable? = " + repoCapabilities.isAllVersionsSearchableSupported());
        System.out.println("getDescendantSupported? = " + repoCapabilities.isGetDescendantsSupported());
        System.out.println("getFolderTreeSupported? = " + repoCapabilities.isGetFolderTreeSupported());
        System.out.println("multiFilingSupported? = " + repoCapabilities.isMultifilingSupported());
        System.out.println("privateWorkingCopySearchable? = " + repoCapabilities.isPwcSearchableSupported());
        System.out.println("pwcUpdateable? = " + repoCapabilities.isPwcUpdatableSupported());
        System.out.println("unfilingSupported? = " + repoCapabilities.isUnfilingSupported());
        System.out.println("versionSpecificFilingSupported? = " + repoCapabilities.isVersionSpecificFilingSupported());
    }

    public void searchMetadataAndFTS(Session session) {
        // Check if the repo supports Metadata search and Full Text Search (FTS)
        RepositoryInfo repoInfo = session.getRepositoryInfo();
        if (repoInfo.getCapabilities().getQueryCapability().equals(CapabilityQuery.METADATAONLY)) {
            logger.warn("Repository does not support FTS [repoName=" + repoInfo.getProductName() +
                    "][repoVersion=" + repoInfo.getProductVersion() + "]");
        } else {
            String query = "SELECT * FROM cmis:document WHERE cmis:name LIKE 'OpenCMIS%'";
            ItemIterable<QueryResult> searchResult = session.query(query, false);
            logSearchResult(query, searchResult);

            query = "SELECT * FROM cmis:document WHERE cmis:name LIKE 'OpenCMIS%' AND CONTAINS('testing')";
            searchResult = session.query(query, false);
            logSearchResult(query, searchResult);
        }
    }

    private void logSearchResult(String query, ItemIterable<QueryResult> searchResult) {
        logger.info("Results from query " + query);
        int i = 1;
        for (QueryResult resultRow : searchResult) {
            logger.info("--------------------------------------------\n" + i + " , "
                    + resultRow.getPropertyByQueryName("cmis:objectId").getFirstValue() + " , "
                    + resultRow.getPropertyByQueryName("cmis:objectTypeId").getFirstValue() + " , "
                    + resultRow.getPropertyByQueryName("cmis:name").getFirstValue());
            i++;
        }
    }

    /**
     * Get a CMIS Object by name from a specified folder.
     *
     * @param parentFolder the parent folder where the object might exist
     * @param objectName   the name of the object that we are looking for
     * @return the Cmis Object if it existed, otherwise null
     */
    private CmisObject getObject(Session session, Folder parentFolder, String objectName) {
        CmisObject object = null;

        try {
            String path2Object = parentFolder.getPath();
            if (!path2Object.endsWith("/")) {
                path2Object += "/";
            }
            path2Object += objectName;
            object = session.getObjectByPath(path2Object);
        } catch (CmisObjectNotFoundException nfe0) {
            // Nothing to do, object does not exist
        }

        return object;
    }

    /**
     * Get the absolute path to the passed in Document object.
     * Called the primary folder path in the Alfresco world as most documents only have one parent folder.
     *
     * @param document the Document object to get the path for
     * @return the path to the passed in Document object, or "Un-filed/{object name}" if it does not have a parent folder
     */
    private String getDocumentPath(Document document) {
        String path2Doc = getParentFolderPath(document);
        if (!path2Doc.endsWith("/")) {
            path2Doc += "/";
        }
        path2Doc += document.getName();
        return path2Doc;
    }

    /**
     * Get the parent folder path for passed in Document object
     *
     * @param document the document object to get the path for
     * @return the parent folder path, or "Un-filed" if the document is un-filed and does not have a parent folder
     */
    private String getParentFolderPath(Document document) {
        Folder parentFolder = getDocumentParentFolder(document);
        return parentFolder == null ? "Un-filed" : parentFolder.getPath();
    }

    /**
     * Get the parent folder for the passed in Document object.
     * Called the primary parent folder in the Alfresco world as most documents only have one parent folder.
     *
     * @param document the Document object to get the parent folder for
     * @return the parent Folder object, or null if it does not have a parent folder and is un-filed
     */
    private Folder getDocumentParentFolder(Document document) {
        // Get all the parent folders (could be more than one if multi-filed)
        List<Folder> parentFolders = document.getParents();

        // Grab the first parent folder
        if (parentFolders.size() > 0) {
            if (parentFolders.size() > 1) {
                logger.info("The " + document.getName() + " has more than one parent folder, it is multi-filed");
            }

            return parentFolders.get(0);
        } else {
            logger.info("Document " + document.getName() + " is un-filed and does not have a parent folder");
            return null;
        }
    }

    /**
     * Returns date as a string
     *
     * @param date date object
     * @return date as a string formatted with "yyyy-MM-dd HH:mm:ss z"
     */
    private String date2String(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(date);
    }

    /**
     * Extracts the various CMIS 1.0 webservices endpoints from the wsdl file at the repository url.
     * This is how we determine whether the webservices url is valid
     *
     * @param url for the wsdl file
     * @return
     */
    public boolean isValidWebServicesUrls(URL url) {
        try {
            Map<String, String> servicesListURL;
            File wsdlTemp = File.createTempFile("repoWSDL", ".xml"); //create temp file
            FileUtils.copyURLToFile(url, wsdlTemp);// copy the wsdl document so we can extract service endpoints
            SAXBuilder builder = new SAXBuilder();
            org.jdom.Document wsdlDoc = builder.build(wsdlTemp);
            Element rootNode = wsdlDoc.getRootElement();//extract root xml document
            Namespace defaultNamespace = rootNode.getNamespace();
            //never use the other method as it returns nothing. http://stackoverflow.com/a/12582380/107301
            List<Element> servicesList = rootNode.getChildren("service", defaultNamespace);
            servicesListURL = servicesList.stream().map(this::extractEndpoints).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            if (servicesListURL.size() > 1)
                return true;
        } catch (Exception ge) {
            System.out.println("************ Error ***********\n\t\t\t" + ge.getMessage() + "\n\n=== Full stack trace ===\n");
            ge.printStackTrace();
        }
        return false;
    }

    /**
     * Self explanatory
     * @param session
     * @return
     */
    public JsonObject getRepositoryInfo(Session session) {
        RepositoryInfo repositoryInfo = session.getRepositoryInfo();

        InputStream tmp = new ByteArrayInputStream(JSONConverter.convert(repositoryInfo, null, null, true)
                .toString().getBytes(StandardCharsets.UTF_8));
        JsonReader rdr = Json.createReader(tmp);
        return rdr.readObject();
    }

    /**
     * Returns the properties and, optionally, the content stream of a document
     * @param session cmis session
     * @param documentObjectId the document objectId
     * @param includeContentStream boolean value which specifies whether to return the content stream of the document
     * @return
     */
    public JsonObject getDocument(Session session, String documentObjectId, boolean includeContentStream) {
        JsonObjectBuilder documentBuilder = Json.createObjectBuilder();
        try {
            Document document = (Document) session.getObject(documentObjectId);
            JsonObject tmp = this.extractUsefulProperties(document);
            documentBuilder.add("properties", tmp);
            if(includeContentStream){
                documentBuilder.add("content", IOUtils.readAllLines(document.getContentStream().getStream()));
            }

        } catch (Exception ge) {
            throw new ErmsIOException("Unable to read document:\n" + ge.getMessage());
        }

        return documentBuilder.build();
    }

    /**
     * returns the properties of a folder and its children
     * @param session
     * @return Json object that represents the folder
     */
    public JsonObject getFolder(Session session, String folderObjectId) {
        RepositoryCapabilities caps = session.getRepositoryInfo().getCapabilities();
        JsonObjectBuilder folderBuilder = Json.createObjectBuilder();
        if (!caps.isGetDescendantsSupported())
            throw new ErmsNotSupportedException("The operation requested is not supported by the repository");

        try {
            Folder folder = (Folder) session.getObject(folderObjectId);
            //Just to be sure
            if( !folder.getId().equalsIgnoreCase(folderObjectId) )
                throw new ErmsRuntimeException("Folder Id mismatch. Please contact system administrator to resolve the");
            CmisSessionWorker cmisSessionWorker = new CmisSessionWorkerImpl(session);
            List<CmisObject> children = cmisSessionWorker.getFolderChildren(folder.getId());
            List<JsonObject> jsonRep = children.stream().map(this::extractUsefulProperties).collect(Collectors.toList());
            JsonArrayBuilder cb =  Json.createArrayBuilder();
            JsonObject tmp = this.extractUsefulProperties(folder);
            for(JsonObject obj : jsonRep)
                cb.add(obj);
            folderBuilder.add("properties", tmp);
            folderBuilder.add("children", cb.build());

        } catch (Exception ge) {
            throw new ErmsIOException("Unable to read folder items for root folder:\n" + ge.getMessage());
        }

        return folderBuilder.build();
    }

    /**
     * returns the root folder
     * @param session
     * @return
     */
    public JsonObject getRootFolder(Session session) {
        RepositoryCapabilities caps = session.getRepositoryInfo().getCapabilities();
        JsonObjectBuilder rootFolder = Json.createObjectBuilder();
        if (!caps.isGetDescendantsSupported())
            throw new ErmsNotSupportedException("The operation requested is not supported by the repository");

        try {
            Folder root = session.getRootFolder();
            CmisSessionWorker cmisSessionWorker = new CmisSessionWorkerImpl(session);
            List<CmisObject> children = cmisSessionWorker.getFolderChildren(root.getId());
            List<JsonObject> jsonRep = children.stream().map(this::extractUsefulProperties).collect(Collectors.toList());
            JsonArrayBuilder cb =  Json.createArrayBuilder();
            JsonObject tmp = this.extractUsefulProperties((CmisObject) root);
            for(JsonObject obj : jsonRep)
                cb.add(obj);
            rootFolder.add("properties", tmp);
            rootFolder.add("children", cb.build());

        } catch (Exception ge) {
            throw new ErmsIOException("Unable to read folder items for root folder:\n" + ge.getMessage());
        }

        return rootFolder.build();
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
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_LENGTH, doc.getContentStreamLength());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_MIMETYPE, doc.getContentStreamMimeType());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_ID, doc.getContentStreamId());
                jsonBuilder.add(ErmsBaseTypes.CONTENT_STREAM_FILENAME, doc.getContentStreamFileName());
                break;
            case "cmis:folder":
                jsonBuilder.add(ErmsBaseTypes.BASETYPE_ID, "folder");
                break;
            default: Utils.getPropertyPostFixValue(cmisObject.getBaseTypeId().value());
                break;
        }
        jsonBuilder.add(ErmsBaseTypes.OBJECT_ID, cmisObject.getId() );
        jsonBuilder.add(ErmsBaseTypes.OBJECT_TYPE_ID, cmisObject.getBaseTypeId().value() );
        jsonBuilder.add(ErmsBaseTypes.NAME, cmisObject.getName() );
        jsonBuilder.add(ErmsBaseTypes.CREATION_DATE, Utils.convertToISO8601Date(cmisObject.getCreationDate()) );
        jsonBuilder.add(ErmsBaseTypes.CREATED_BY, cmisObject.getCreatedBy() );
        jsonBuilder.add(ErmsBaseTypes.LAST_MODIFIED, cmisObject.getLastModifiedBy() );

        return jsonBuilder.build();
    }

    /**
     * returns a Pair<String,String> containing the service name and it's webservices endpoint url from a
     * jDom xml element which is in turn extracted from a the wsdl.xml
     *
     * @param svc xml jdom element representing the webservice endpoint
     * @return
     */
    private ImmutablePair<String, String> extractEndpoints(Element svc) {
        Namespace defaultNamespace = svc.getNamespace();
        Namespace soapNamespace = svc.getNamespace("soap");
        return new ImmutablePair<>(svc.getAttributeValue("name"), svc.getChild("port", defaultNamespace)
                .getChild("address", soapNamespace)
                .getAttributeValue("location")
        );
    }

    /**
     * Just a short simple test against the CMIS repository to see if it's alive by retrieving a few attributes about it
     *
     * @param profile
     */
    public void testCMISConnection(Profile profile) {
        try {
            Cmis1Connector tstConnector = new Cmis1Connector();
            Session tstSess = tstConnector.getSession(profile);
            RepositoryInfo repoInfo = tstSess.getRepositoryInfo();

            System.out.println("RepositoryId => " + repoInfo.getId());
            System.out.println("Supported CMIS version => " + repoInfo.getCmisVersionSupported());
            System.out.println("Root Folder ID - " + repoInfo.getRootFolderId());
            System.out.println("Listing capabilities......\n");
            listRepoCapabilities(repoInfo);
        } catch (Exception ge) {
            logger.warn("There was an exception testing the repository's CMIS capabilities: \n" + ge.getMessage());
        }
    }



}
