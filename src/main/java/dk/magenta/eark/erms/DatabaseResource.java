package dk.magenta.eark.erms;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

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
  @Path("getRepositories")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public JsonObject getRepositories() {
    try {
      JsonObject json = databaseConnectionStrategy.selectRepositories();
      return json;
    } catch (SQLException e) {
      JsonObjectBuilder builder = Json.createObjectBuilder();
      builder.add(Constants.SUCCESS, false);
      builder.add(Constants.ERRORMSG, e.getMessage());
      return builder.build();
    }
  }

  @GET
  @Path("test")
  public String test() {
    return "It's working!";
  }
}
