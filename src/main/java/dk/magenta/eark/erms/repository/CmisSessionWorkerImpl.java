package dk.magenta.eark.erms.repository;

import dk.magenta.eark.erms.ErmsBaseTypes;
import dk.magenta.eark.erms.Utils;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.spi.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lanre.
 */
public class CmisSessionWorkerImpl implements CmisSessionWorker {
    private Session session;
    private ObjectFactory objectFactory;
    private OperationContext operationContext;

    /**
     * Constructor
     *
     * @param session
     */
    public CmisSessionWorkerImpl(Session session) {
        this.session = session;
        this.objectFactory = session.getObjectFactory();
        this.operationContext = session.createOperationContext();
    }

    //<editor-fold desc="Webservices endpoints">

    /**
     * Returns the Navigation service
     *
     * @return
     */
    @Override
    public NavigationService getNavigationService() {
        return session.getBinding().getNavigationService();
    }

    /**
     * Returns the Repository service
     *
     * @return
     */
    @Override
    public RepositoryService getRepositoryService() {
        return session.getBinding().getRepositoryService();
    }

    /**
     * Returns the Versioning service
     *
     * @return
     */
    @Override
    public VersioningService getVersioningService() {
        return session.getBinding().getVersioningService();
    }

    /**
     * Returns the AclService service
     *
     * @return
     */
    @Override
    public AclService getACLService() {
        return session.getBinding().getAclService();
    }

    /**
     * Returns the Relationship service
     *
     * @return
     */
    @Override
    public RelationshipService getRelationshipService() {
        return session.getBinding().getRelationshipService();
    }

    /**
     * Returns the Policy service
     *
     * @return
     */
    @Override
    public PolicyService getPolicyService() {
        return session.getBinding().getPolicyService();
    }

    /**
     * Returns the Object service
     *
     * @return
     */
    @Override
    public ObjectService getObjectService() {
        return session.getBinding().getObjectService();
    }

    /**
     * Returns the Discovery service
     *
     * @return
     */
    @Override
    public DiscoveryService getDiscoveryService() {
        return session.getBinding().getDiscoveryService();
    }

    /**
     * Returns the MultiFiling service
     *
     * @return
     */
    @Override
    public MultiFilingService getMultiFilingService() {
        return session.getBinding().getMultiFilingService();
    }
    //</editor-fold>

    /**
     * Returns a list of CmisObject representing the children of a folder given it's cmis object id
     *
     * @return
     */
    @Override
    public List<CmisObject> getFolderChildren(String folderObjectId) {
        List<ObjectInFolderData> children = getNavigationService().getChildren(session.getRepositoryInfo().getId(), folderObjectId,
                null, null, false, IncludeRelationships.BOTH, null, false, null, null, null).getObjects();
        return children.stream().map(t -> objectFactory.convertObject(t.getObject(), this.operationContext)).collect(Collectors.toList());
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

}
