package dk.magenta.eark.erms;

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

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("addRepository")
  public JsonObject addRespository(JsonObject json) {
    JsonObjectBuilder builder = Json.createObjectBuilder();

    try {
      String profileName = json.getString("profileName");
      String url = json.getString("url");
      String userName = json.getString("userName");
      String password = json.getString("password");
    } catch (NullPointerException e) {
      builder.add("success", false);
      builder.add("error", "Malformed JSON received!");
    }

    return builder.build();
  }

  @GET
  @Path("test")
  public String test() {
    return "It's working!";
  }
}
