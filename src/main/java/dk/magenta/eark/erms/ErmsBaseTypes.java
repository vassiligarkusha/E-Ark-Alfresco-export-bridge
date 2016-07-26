package dk.magenta.eark.erms;

/**
 * Created to bridge with the cmis base types
 * @author DarkStar1.
 */
public interface ErmsBaseTypes {
    String OBJECT_ID = "objectId";
    String BASETYPE_ID = "type";
    String PATH = "path";
    String NAME = "name";
    String PARENT_ID = "parentId";
    String CREATED_BY = "createdBy";
    String CREATION_DATE = "creationDate";
    String OBJECT_TYPE_ID = "objectTypeId";
    String DESCRIPTION = "description";
    String LAST_MODIFIED = "lastModifiedBy";
    String LAST_MOD_DATE = "lastModificationDate";

    //Some props that are for documents
    String CONTENT_STREAM_LENGTH = "contentStreamLength";
    String CONTENT_STREAM_FILENAME ="contentStreamFileName"; //Actual filename
    String CONTENT_STREAM_ID = "contentStreamId";
    String CONTENT_STREAM_MIMETYPE = "contentStreamMimeType";

}
