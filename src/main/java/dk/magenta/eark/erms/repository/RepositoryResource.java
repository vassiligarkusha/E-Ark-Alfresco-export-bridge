package dk.magenta.eark.erms.repository;

import java.net.URLDecoder;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.magenta.eark.erms.Constants;
import dk.magenta.eark.erms.db.DatabaseConnectionStrategy;
import dk.magenta.eark.erms.db.JDBCConnectionStrategy;
import dk.magenta.eark.erms.exceptions.ErmsRuntimeException;
import dk.magenta.eark.erms.json.JsonUtils;
import dk.magenta.eark.erms.repository.profiles.Profile;
import dk.magenta.eark.erms.system.PropertiesHandlerImpl;

/**
 * @author lanre, andreas
 */


@Path("repository")
public class RepositoryResource {

    public static final String FOLDER_OBJECT_ID = "folderObjectId";
    public static final String DOCUMENT_OBJECT_ID = "documentObjectId";
    public static final String MAP_NAME = "mapName";

    private final Logger logger = LoggerFactory.getLogger(RepositoryResource.class);

    private Cmis1Connector cmis1Connector;
    DatabaseConnectionStrategy dbConnectionStrategy;

    public RepositoryResource() {

        try {
            this.cmis1Connector = new Cmis1Connector();
            this.dbConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl("settings.properties"));
        } catch (SQLException sqe) {
            System.out.println("====> Error <====\nUnable to acquire db resource due to: " + sqe.getMessage());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("connect")
    public JsonObject connect(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonObject response;
        if (json.containsKey(Profile.NAME) && json.containsKey(MAP_NAME) ) {
            String profileName = json.getString(Profile.NAME);
            String mapName = json.getString(MAP_NAME);

            try {
                //Get a session worker
                CmisSessionWorker sessionWorker =this.getSessionWorker(profileName);

                //Build the json for the repository info
                response = sessionWorker.getRepositoryInfo();
                builder.add("repositoryInfo", response);
                builder.add("rootFolder", sessionWorker.getRootFolder());

            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "The connection profile does not have a name!");
        }

        return builder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("getDocument")
    public JsonObject Document(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (json.containsKey(DOCUMENT_OBJECT_ID) && json.containsKey(Profile.NAME)) {
            String profileName = json.getString(Profile.NAME);
            String documentObjectId = json.getString(DOCUMENT_OBJECT_ID);
            boolean includeContentStream = json.getBoolean("includeContentStream", false);

            try {
                //Get a session worker
                CmisSessionWorker sessionWorker =this.getSessionWorker(profileName);

                //Build the json for the repository info
                builder.add("document", sessionWorker.getDocument(documentObjectId, includeContentStream));

            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "The connection profile does not have a name!");
        }

        return builder.build();
    }

    /**
     * Just returns a folder object
     * @param json
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("getFolder")
    public JsonObject getFolder(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (json.containsKey(FOLDER_OBJECT_ID) && json.containsKey(Profile.NAME)) {

            String profileName = json.getString(Profile.NAME);
            String folderObjectId = json.getString(FOLDER_OBJECT_ID);

            try {
                CmisSessionWorker cmisSessionWorker = this.getSessionWorker(profileName);

                //Build the json for the repository info
                builder.add("folder", cmisSessionWorker.getFolder(folderObjectId));

            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "The connection profile does not have a name!");
        }

        return builder.build();
    }

    /**
     *
     * @param profileName
     * @param objectId
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("isroot/{objectId}/in/{profileName}")
    public JsonObject isROOT(@PathParam("objectId") String objectId, @PathParam("profileName") String profileName) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (StringUtils.isNotBlank(objectId) && StringUtils.isNotBlank(profileName)) {
            try {
                profileName = URLDecoder.decode(profileName, "UTF-8");
                objectId = URLDecoder.decode(objectId, "UTF-8");
                CmisSessionWorker cmisSessionWorker = this.getSessionWorker(profileName);
                JsonObject rootFolder = cmisSessionWorker.getRootFolder();
                String repoRoot =  rootFolder.getJsonObject("properties").getString("objectId") ;

                //Build the json for the repository info
                builder.add("isRoot", objectId.equalsIgnoreCase(repoRoot));

            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "One or more parameters missing or malformed");
        }

        return builder.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("extract")
    public JsonObject extract(JsonObject json) {
    	JsonObjectBuilder builder = Json.createObjectBuilder();
    	
    	// Check if the mandatory keys are in the request JSON 
    	String[] mandatoryJsonKeys = {Profile.NAME, "exportList", "excludeList"};
    	if (JsonUtils.containsCorrectKeys(json, mandatoryJsonKeys)) {
    		
    		// Check that the exportList is not empty etc...
    		if (JsonUtils.isArrayValid(json, "exportList")) {
    		
    		Session session = getSessionWorker(json.getString(Profile.NAME)).getSession();
    		JsonArray exportList = json.getJsonArray("exportList");
    		for (int i = 0; i < exportList.size(); i++) {
    			// We know (assume) that the values are strings
    			String objectId = exportList.getString(i);
    			
    		}
    		
    		} else {
    			JsonUtils.addArrayErrorMessage(builder, "exportList");
    		}
    		
    	} else {
    		JsonUtils.addKeyErrorMessage(builder, mandatoryJsonKeys);
    	}
    	return builder.build();
    }
    
    /**
     * Returns a cmis session worker instance given a profile name
     * @param profileName
     * @return
     */
    private CmisSessionWorker getSessionWorker(String profileName){
        try{
            //Retrieve the connection profile
            Profile connProfile = this.dbConnectionStrategy.getProfile(profileName);
            //Get a CMIS session object
            Session repoSession = this.cmis1Connector.getSession(connProfile);
            //Instantiate a session worker
            return new CmisSessionWorkerImpl(repoSession);
        }
        catch(Exception ge){
            logger.error("Unable to create session worker due to: " + ge.getMessage());
            throw new ErmsRuntimeException(ge.getMessage());
        }
    }
}
