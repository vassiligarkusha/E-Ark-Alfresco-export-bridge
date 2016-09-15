package dk.magenta.eark.erms.repository.profiles;

import dk.magenta.eark.erms.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Path("profile")
public class ProfilesResource {
    private final Logger logger = LoggerFactory.getLogger(ProfilesResource.class);

    private ProfilesWorker profilesWorker;

    public ProfilesResource() {
        try {
            profilesWorker = new ProfilesWorkerImpl();
        } catch (Exception ge) {
            System.out.println("====> Error <====\nUnable to initialise profile : " + ge.getMessage());
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
            List<String> roots;

            if (json.containsKey(Profile.ROOT_REPOSITORIES) && StringUtils.isNotEmpty(json.get(Profile.ROOT_REPOSITORIES).toString())) {
                JsonArray repoList = json.getJsonArray(Profile.ROOT_REPOSITORIES);
                roots = repoList.stream().filter(t -> StringUtils.isNotBlank(t.toString()))
                                .map(t -> StringUtils.strip(t.toString(),"\"")).collect(Collectors.toList());
            } else roots = Collections.EMPTY_LIST;

            try {
                this.profilesWorker.createProfile(profileName, url, userName, password, roots);
            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "Unable to create profile. Mandatory property missing!");
        }

        return builder.build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("delete/{profileName}")
    public JsonObject deleteProfile(@PathParam("profileName") final String profileName) {
        return this.profilesWorker.deleteProfile(profileName);
    }

    @GET
    @Path("getProfiles")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonObject getProfiles() {
        return this.profilesWorker.getProfiles();
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

            this.profilesWorker.updateProfile(profileName, url, userName, password);


            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "Malformed JSON received!");
        }

        return builder.build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("add/{profileName}/repository/{repository}")
    public JsonObject addProfileToRepo(@PathParam("profileName") final String profileName,
                                       @PathParam("repository") final String repository ) {
        return this.profilesWorker.addRepoToProfile(profileName, repository);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("remove/{profileName}/repository/{repository}")
    public JsonObject removeProfileFromRepo(@PathParam("profileName") final String profileName,
                                            @PathParam("repository") final String repository ) {

        return this.profilesWorker.removeRepoFromProfile(profileName, repository);
    }


}
