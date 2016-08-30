package dk.magenta.eark.erms.repository.profiles;

import javax.json.JsonObject;
import java.util.List;

/**
 * @author lanre.
 */
public interface ProfilesWorker {

    /**
     * Create a new profile in the repository
     * @param profileName The name for the Profile. Must be unique.
     * @param url The url of where to rerieve the wsdl for the repository concerned
     * @param userName The authprity username  with access to the repository
     * @param password The password for the username for the repository
     * @return a Json object containing {success: true | false, message : "some error message | created" }
     */
    JsonObject createProfile(String profileName, String url, String userName, String password, List<String> roots);

    /**
     * Return the list of profiles saved in the system
     * @return Json object conatining the list of profiles retrieved
     */
    JsonObject getProfiles();

    /**
     * Updates the other details of a profile given its profile name (the only property that can not be updated)
     * Any property not required should be set to null
     * @param profileName The name of the profile to update
     * @param url the .wsdl url for the repository
     * @param userName userName of the authority with access to the repository concerned
     * @param password password the password for the userName
     * @return a Json object containing {success: true | false, message : "some error message | updated"
     */
    JsonObject updateProfile(String profileName, String url, String userName, String password);

    /**
     * Deletes a profile from the db
     * @param ProfileName name of the profile to delete
     * @return a Json object containing {success: true | false, message : "some error message | created" }
     */
    JsonObject deleteProfile(String profileName);
}
