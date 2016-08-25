package dk.magenta.eark.erms;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.json.JsonObject;
import java.sql.SQLException;

public interface DatabaseConnectionStrategy {
    /**
     * Insert new repository into DB
     *
     * @param profileName
     * @param url
     * @param userName
     * @param password
     */
    void insertRepository(String profileName, String url, String userName, String password) throws SQLException;

    /**
     * Updates a profile in the db
     *
     * @param profile
     * @throws SQLException
     */
    void updateProfile(Profile profile) throws SQLException;

    /**
     * Gets a single profile from the db using the profile name (Profile name is unique)
     *
     * @param profileName the name of the profile to retrieve from the db
     * @return
     * @throws SQLException
     */
    Profile getProfile(String profileName) throws SQLException;

    JsonObject selectRepositories() throws SQLException;

    /**
     * Persists the information about the saved file into the db
     * @param mapName the name with which to give the mapping file
     * @param sysPath the location of the actual mapping file on disk
     * @return
     */
    boolean saveMapping(String mapName,String sysPath, FormDataContentDisposition fileMetadata);
}
