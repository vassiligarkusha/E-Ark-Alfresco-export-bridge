package dk.magenta.eark.erms;

import javax.json.JsonObject;
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
  public JsonObject addRespository(JsonObject j) {
    return j;
  }

  @GET
  @Path("test")
  public String test() {
    return "It's working!";
  }
}
