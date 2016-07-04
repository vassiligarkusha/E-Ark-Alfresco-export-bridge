package dk.magenta.eark.erms;

import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("database")
public class DatabaseResource {

  private DatabaseConnectionStrategy databaseConnectionStrategy;

  public DatabaseResource() {
    databaseConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl("settings.properties"));
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("addRepository")
  public JsonObject addRespository(JsonObject json) {
    JsonObjectBuilder builder = Json.createObjectBuilder();

    if (json.containsKey(Profile.PROFILENAME) && json.containsKey(Profile.URL) && json.containsKey(Profile.USERNAME)
      && json.containsKey(Profile.PASSWORD)) {

      String profileName = json.getString(Profile.PROFILENAME);
      String url = json.getString(Profile.URL);
      String userName = json.getString(Profile.USERNAME);
      String password = json.getString(Profile.PASSWORD);

      try {
        databaseConnectionStrategy.insertRepository(profileName, url, userName, password);
      } catch (SQLException e) {
        builder.add(Constants.SUCCESS, false);
        builder.add(Constants.ERRORMSG, e.getMessage());
      }

      builder.add(Constants.SUCCESS, true);

    } else {
      builder.add(Constants.SUCCESS, false);
      builder.add(Constants.ERRORMSG, "Malformed JSON received!");
    }

    return builder.build();
  }

  @GET
  @Path("test")
  public String test() {
    return "It's working!";
  }
}
