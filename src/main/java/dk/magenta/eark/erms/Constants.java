package dk.magenta.eark.erms;

public class Constants {

	// Accepted extraction formats
	public static final String EXTRACTION_FORMAT_EARKSIP = "earkSIP";

	// JSON keys
	public static final String NAME = "name";
	public static final String SUCCESS = "success";
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String ERRORMSG = "error";
	public static final String EXPORT_LIST = "exportList";
	public static final String EXCLUDE_LIST = "excludeList";
	public static final String MAP_NAME = "mapName";
	public static final String EXPORT_PATH = "exportPath";

	//search related
	public static final String DEFAULT_PERDICATE = "OR";
	public static final String PERDICATE = "predicate";
	public static final String SELECTED = "selected";
	public static final String SEARCH_TERM = "searchTerm";
	public static final String SEARCH_TERMS = "searchTerms";
    public static final String DEFAULT_OBJECT_SCOPE = "*";
	public static final String OBJECT_SCOPE = "objectScope";

	// File paths
	public static final String SETTINGS = "settings.properties";
	public static final String CMIS_SETTINGS_PATH = "cmis.properties";

	// Namespaces
	public static final String MAPPING_NAMESPACE = "http://www.magenta.dk/eark/erms/mapping/1.0";
	public static final String EAD_NAMESPACE = "http://ead3.archivists.org/schema/";

	//ERMS types
	public static final String OBJECT_ID = "objectId";
	public static final String BASETYPE_ID = "type";
	public static final String PATH = "path";
	public static final String PARENT_ID = "parentId";
	public static final String CREATED_BY = "createdBy";
	public static final String CREATION_DATE = "creationDate";
	public static final String OBJECT_TYPE_ID = "objectTypeId";
	public static final String DESCRIPTION = "description";
	public static final String LAST_MODIFIED = "lastModifiedBy";
	public static final String LAST_MOD_DATE = "lastModificationDate";

	//Some props that are for documents
	public static final String CONTENT_SIZE = "size";
	public static final String CONTENT_STREAM_LENGTH = "contentStreamLength";
	public static final String CONTENT_STREAM_FILENAME ="contentStreamFileName"; //Actual filename
	public static final String CONTENT_STREAM_ID = "contentStreamId";
	public static final String CONTENT_STREAM_MIMETYPE = "contentStreamMimeType";

}
