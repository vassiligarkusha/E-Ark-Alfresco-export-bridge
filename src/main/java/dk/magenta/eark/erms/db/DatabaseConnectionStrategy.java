package dk.magenta.eark.erms.db;

import dk.magenta.eark.erms.mappings.Mapping;
import dk.magenta.eark.erms.repository.profiles.Profile;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseConnectionStrategy {
    /**
     * Returns a list of profiles from the db
     * @return
     * @throws SQLException
     */
    List<Profile> getProfiles() throws SQLException;

    /**
     * Updates a profile in the db
     *
     * @param profile
     * @throws SQLException
     */
    boolean updateProfile(Profile profile) throws SQLException;

    /**
     * Removes a single profile from the repository
     * @param profileName the name of the profile to remove
     * @return
     * @throws SQLException
     */
    boolean deleteProfile(String profileName) throws SQLException;

    /**
     * Gets a single profile from the db using the profile name (Profile name is unique)
     *
     * @param profileName the name of the profile to retrieve from the db
     * @return
     * @throws SQLException
     */
    Profile getProfile(String profileName) throws SQLException;

    /**
     * Adds the profile to the db
     * @param profile
     * @return true | false
     * @throws SQLException
     */
    boolean createProfile(Profile profile) throws SQLException;

    /**
     * Removes a mapping from db given the system mapping name
     *
     * @param mappingName the name of the mapping to delete
     * @return a boolean indicating success
     * @throws SQLException
     */
    boolean deleteMapping(String mappingName) throws SQLException;

    /**
     * Gets a mapping from db given the system mapping name
     * @param mappingName the name of the mapping to retrieve
     * @return json object representing the mapping
     * @throws SQLException
     */
    Mapping getMapping(String mappingName) throws SQLException;

    /**
     * Gets all mappings on the system
     * @return
     */
    List getMappings() throws SQLException;

    /**
     * Persists the information about the saved file into the db
     * @param mapName the name with which to give the mapping file
     * @param sysPath the location of the actual mapping file on disk
     * @return
     */
    boolean saveMapping(String mapName,String sysPath, FormDataContentDisposition fileMetadata) throws SQLException;
    /**
     * returns whether the repository root exists for that profile
     * @param profileName the name of the profile for which we want to check
     * @param repositoryRoot the repository root name
     * @return
     * @throws SQLException
     */
    boolean repositoryRootExists(String profileName, String repositoryRoot) throws SQLException;

    /**
     * Add a repository root from a profile
     * @param profileName the profile name for which we wish to add a repository root
     * @param repositoryRoot the name of the repository root we wish to add
     * @return
     * @throws SQLException
     */
    boolean addRepoRoot(String profileName, String repositoryRoot) throws SQLException;

    /**
     * Remove a repository root from a profile
     * @param profileName the profile name for which we wish to subtract a repository root
     * @param repositoryRoot the name of the repository root we wish to remove
     * @return
     * @throws SQLException
     */
    boolean removeRepoRoot(String profileName, String repositoryRoot) throws SQLException;

}
