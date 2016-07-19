package dk.magenta.eark.erms.repository;

import dk.magenta.eark.erms.*;
import org.apache.chemistry.opencmis.client.api.Session;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
                builder.add("RepositoryInfo", response);

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
