# Backend for the Alfresco Export Module

## Extracting Resources

In order use the extraction service, use the following resources:
(**NOTE** only one extraction can run at a time)

#### POST /webapi/extraction/extract

Send JSON like this:
(**NOTE** The CMIS nodes in the exportList MUST all be at the same semantic level!)

```
{
  "name": "this-is-the-profile-name",
  "mapName": "this-is-the-name-of-the-mapping-profile",
  "exportList": ["CmisObjectId1", "CmisObjectId3", ...],
  "excludeList": ["CmisObjectId3", "CmisObjectId4", ...]
}
```

In the case of success, you will get a JSON response saying

```
{
	"success": true,
	"message": "Extraction initiated - check /status for error messages"
}
```

In case of an unsuccessful extraction initiation, "success" will be `false` , and you will get a JSON message 
describing what the problem was.

#### GET /webapi/extraction/status

In case of success the backend will respond with a status of 
RUNNING, NOT_RUNNING or DONE. For example:

```
{
	"success": true,
	"message": "RUNNING"
}
```

If an error occured, "success" will be `false` and an appropriate error message will 
be returned.

#### GET /webapi/extraction/terminate

Will terminate a running process and return JSON indicating whether the 
termination was successful or not, i.e. 

```
{
	"success": true,
	"message": "Process terminated"
}
```

or 

```
{
	"success": false,
	"message": "No processes are running"
}
```

or

```
{
	"success": false,
	"message": "Process already done"
}
```

#### POST /webapi/extraction/ead/upload

Use to upload the EAD template. Send the EAD template XML file using multipart/form-data. 
The following key/value pairs are needed:

* key = `eadFile`, value = `name-of-ead-template-file`
* key = `file`, value = `stream-containing-the-file-content`