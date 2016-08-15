package dk.magenta.eark.erms.repository;

import dk.magenta.eark.erms.*;
import dk.magenta.eark.erms.exceptions.ErmsRuntimeException;

import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.sql.SQLException;

/**
 * @author lanre.
 */


@Path("repository")
public class Repository {

    private final Logger logger = LoggerFactory.getLogger(Repository.class);

    private Cmis1Connector cmis1Connector;
    DatabaseConnectionStrategy dbConnectionStrategy;

    public Repository() {

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
        if (json.containsKey(Profile.PROFILENAME)) {
            String profileName = json.getString(Profile.PROFILENAME);

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
        if (json.containsKey(Profile.DOCUMENT_OBJECT_ID) && json.containsKey(Profile.PROFILENAME)) {
            String profileName = json.getString(Profile.PROFILENAME);
            String documentObjectId = json.getString(Profile.DOCUMENT_OBJECT_ID);
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
        if (json.containsKey(Profile.FOLDER_OBJECT_ID) && json.containsKey(Profile.PROFILENAME)) {

            String profileName = json.getString(Profile.PROFILENAME);
            String folderObjectId = json.getString(Profile.FOLDER_OBJECT_ID);

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
