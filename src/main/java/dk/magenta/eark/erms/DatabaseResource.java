package dk.magenta.eark.erms;

import dk.magenta.eark.erms.Profiles.Profile;
import org.apache.commons.lang3.StringUtils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("database")
public class DatabaseResource {

    private DatabaseConnectionStrategy databaseConnectionStrategy;

    public DatabaseResource() {
        try {
            databaseConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl("settings.properties"));
        } catch (SQLException sqe) {
            System.out.println("====> Error <====\nUnable to acquire db resource due to: " + sqe.getMessage());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("addProfile")
    public JsonObject addProfile(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        if (json.containsKey(Profile.NAME) && json.containsKey(Profile.URL) && json.containsKey(Profile.USERNAME)
                && json.containsKey(Profile.PASSWORD)) {
            String profileName = json.getString(Profile.NAME);
            String url = json.getString(Profile.URL);
            String userName = json.getString(Profile.USERNAME);
            String password = json.getString(Profile.PASSWORD);
            String [] roots;

            if (json.containsKey(Profile.ROOT_REPOSITORIES) && StringUtils.isNotEmpty(json.get(Profile.ROOT_REPOSITORIES).toString())) {
                JsonArray repoList = json.getJsonArray(Profile.ROOT_REPOSITORIES);
                roots = repoList.stream().filter(t -> StringUtils.isNotBlank(t.toString()))
                                .map(t -> StringUtils.strip(t.toString(),"\"")).toArray(String[]::new);
            } else roots = null;

            try {
                databaseConnectionStrategy.insertRepository(profileName, url, userName, password, roots);
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
    @Path("getProfiles")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getProfiles() {
        try {
            return databaseConnectionStrategy.selectRepositories();
        } catch (SQLException e) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, e.getMessage());
            return builder.build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("updateProfile")
    public JsonObject updateProfile(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        if (json.containsKey(Profile.NAME) && json.containsKey(Profile.URL) && json.containsKey(Profile.USERNAME)
                && json.containsKey(Profile.PASSWORD)) {

            String profileName = json.getString(Profile.NAME);
            String url = json.getString(Profile.URL);
            String userName = json.getString(Profile.USERNAME);
            String password = json.getString(Profile.PASSWORD);

            Profile pr = new Profile(profileName, url, userName, password);

            try {
                databaseConnectionStrategy.updateProfile(pr);
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

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("profile/{profileName}/repository/{repository}")
    public JsonObject addProfileToRepo(@PathParam("profileName") final String profileName,
                                        @PathParam("repository") final String repository ) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        try{
            boolean created = databaseConnectionStrategy.addRepoRoot(profileName, repository);
            builder.add(Constants.SUCCESS, created);
        }
        catch(SQLException sqe){
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, sqe.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("profile/{profileName}/repository/{repository}")
    public JsonObject removeProfileFromRepo(@PathParam("profileName") final String profileName,
                                            @PathParam("repository") final String repository ) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        try{
            boolean deleted = databaseConnectionStrategy.removeRepoRoot(profileName, repository);
            builder.add(Constants.SUCCESS, deleted);
        }
        catch(SQLException sqe){
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, sqe.getMessage());
        }
        return builder.build();
    }



    @GET
    @Path("test")
    public String test() {
        return "It's working!";
    }
}
