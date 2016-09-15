package dk.magenta.eark.erms.repository.profiles;


import dk.magenta.eark.erms.Constants;
import dk.magenta.eark.erms.db.DatabaseConnectionStrategy;
import dk.magenta.eark.erms.db.JDBCConnectionStrategy;
import dk.magenta.eark.erms.system.PropertiesHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.SQLException;
import java.util.List;

/**
 * @author lanre.
 */
public class ProfilesWorkerImpl implements ProfilesWorker {
    private final Logger logger = LoggerFactory.getLogger(ProfilesWorkerImpl.class);

    private DatabaseConnectionStrategy databaseConnectionStrategy;

    public ProfilesWorkerImpl() {
        try {
            databaseConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl("settings.properties"));
        }
        catch(SQLException sqe){
            System.out.println("====> Error <====\nUnable to acquire db resource due to: " + sqe.getMessage());
        }
    }

    /**
     * Create a new profile in the repository
     *
     * @param profileName The name for the Profile. Must be unique.
     * @param url         The url of where to rerieve the wsdl for the repository concerned
     * @param userName    The authprity username  with access to the repository
     * @param password    The password for the username for the repository
     * @return a Json object containing {success: true | false, message : "some error message | created" }
     */
    @Override
    public JsonObject createProfile(String profileName, String url, String userName, String password, List<String> roots) {
        Profile profile = new Profile(profileName, url, userName, password, roots);
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        try {
            if (databaseConnectionStrategy.createProfile(profile)) {
                jsonObjectBuilder.add(Constants.SUCCESS, true);
                jsonObjectBuilder.add(Constants.MESSAGE, profile.getName() + " created");
            }
        }
        catch (Exception ge){
            ge.printStackTrace();
            logger.error("Unable to create profile in db.");
            jsonObjectBuilder.add(Constants.SUCCESS, false);
            jsonObjectBuilder.add(Constants.ERRORMSG, ge.getMessage());

        }
        return jsonObjectBuilder.build();
    }

    /**
     * Return the list of profiles saved in the system
     *
     * @return Json object conatining the list of profiles retrieved
     */
    @Override
    public JsonObject getProfiles() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            List<Profile>  pList = databaseConnectionStrategy.getProfiles();
            if(pList.size() > 0){
                pList.forEach(t -> jsonArrayBuilder.add(t.toJsonObject()));
                jsonObjectBuilder.add("profiles", jsonArrayBuilder);
                jsonObjectBuilder.add(Constants.SUCCESS, true);
            }

        } catch (SQLException e) {
            jsonObjectBuilder.add(Constants.SUCCESS, false);
            jsonObjectBuilder.add(Constants.ERRORMSG, e.getMessage());
        }
        return jsonObjectBuilder.build();
    }

    /**
     * Updates the other details of a profile given its profile name (the only property that can not be updated)
     * Any property not required should be set to null
     *
     * @param profileName The name of the profile to update
     * @param url         the .wsdl url for the repository
     * @param userName    userName of the authority with access to the repository concerned
     * @param password    password the password for the userName
     * @return a Json object containing {success: true | false, message : "some error message | updated"
     */
    @Override
    public JsonObject updateProfile(String profileName, String url, String userName, String password) {
        Profile profile = new Profile(profileName, url, userName, password);
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        try {
            if (databaseConnectionStrategy.updateProfile(profile)) {
                jsonObjectBuilder.add(Constants.SUCCESS, true);
                jsonObjectBuilder.add(Constants.MESSAGE, profile.getName() + " created");
            }
        }
        catch (Exception ge){
            ge.printStackTrace();
            logger.error("Unable to create profile in db.");
            jsonObjectBuilder.add(Constants.SUCCESS, false);
            jsonObjectBuilder.add(Constants.ERRORMSG, ge.getMessage());

        }
        return jsonObjectBuilder.build();
    }

    /**
     * Deletes a profile from the db
     *
     * @param profileName name of the profile to delete
     * @return a Json object containing {success: true | false, message : "some error message | deleted" }
     */
    @Override
    public JsonObject deleteProfile(String profileName) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        try {
            if (databaseConnectionStrategy.deleteProfile(profileName)) {
                jsonObjectBuilder.add(Constants.SUCCESS, true);
                jsonObjectBuilder.add(Constants.MESSAGE, profileName + " deleted");
            }
        }
        catch (Exception ge){
            ge.printStackTrace();
            logger.error("Unable to delete profile from db.");
            jsonObjectBuilder.add(Constants.SUCCESS, false);
            jsonObjectBuilder.add(Constants.ERRORMSG, ge.getMessage());

        }
        return jsonObjectBuilder.build();
    }

    /**
     * Adds a browse folder to the profile repository
     * @param profileName
     * @param repositoryName
     * @return
     */
    public JsonObject addRepoToProfile(String profileName, String repositoryName) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        try {
            boolean created = databaseConnectionStrategy.addRepoRoot(profileName, repositoryName);
            builder.add(Constants.SUCCESS, created);
        } catch (SQLException sqe) {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, sqe.getMessage());
        }
        return builder.build();
    }

    /**
     * Removes a browse repository from profile
     * @param profileName
     * @param repositoryName
     * @return
     */
    public JsonObject removeRepoFromProfile(String profileName, String repositoryName) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        try{
            boolean deleted = databaseConnectionStrategy.removeRepoRoot(profileName, repositoryName);
            builder.add(Constants.SUCCESS, deleted);
        }
        catch(SQLException sqe){
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, sqe.getMessage());
        }
        return builder.build();
    }
}
