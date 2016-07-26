package dk.magenta.eark.erms.repository;

import dk.magenta.eark.erms.*;
import org.apache.chemistry.opencmis.client.api.Session;

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

    private Cmis1Connector cmis1Connector;
    DatabaseConnectionStrategy dbConnectionStrategy;

    public Repository() {
        try {
            this.cmis1Connector = new Cmis1Connector();
            this. dbConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl("settings.properties"));
        }
        catch(SQLException sqe){
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
                Profile connProfile = this.dbConnectionStrategy.getProfile(profileName);
                Session repoSession = this.cmis1Connector.getSession(connProfile);

                //Build the json for the repository info
                response = this.cmis1Connector.getRepositoryInfo(repoSession);
                this.cmis1Connector.getRootFolder(repoSession);
                builder.add("repositoryInfo", response);
                builder.add("rootFolder", this.cmis1Connector.getRootFolder(repoSession));

            } catch (SQLException e) {
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
            boolean includeContentstream = json.getBoolean("includeContentStream", false);
            String profileName = json.getString(Profile.PROFILENAME);
            String folderObjectId = json.getString(Profile.DOCUMENT_OBJECT_ID);

            try {
                Profile connProfile = this.dbConnectionStrategy.getProfile(profileName);
                Session repoSession = this.cmis1Connector.getSession(connProfile);

                //Build the json for the repository info
                builder.add("document", this.cmis1Connector.getDocument(repoSession, folderObjectId, includeContentstream));

            } catch (SQLException e) {
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
    @Path("getFolder")
    public JsonObject getFolder(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (json.containsKey(Profile.FOLDER_OBJECT_ID) && json.containsKey(Profile.PROFILENAME)) {


            String profileName = json.getString(Profile.PROFILENAME);
            String folderObjectId = json.getString(Profile.FOLDER_OBJECT_ID);

            try {
                Profile connProfile = this.dbConnectionStrategy.getProfile(profileName);
                Session repoSession = this.cmis1Connector.getSession(connProfile);

                //Build the json for the repository info
                builder.add("folder", this.cmis1Connector.getFolder(repoSession, folderObjectId));

            } catch (SQLException e) {
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

}
