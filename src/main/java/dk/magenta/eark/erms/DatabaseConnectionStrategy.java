package dk.magenta.eark.erms;

import dk.magenta.eark.erms.Profiles.Profile;

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
     *
     * @param profileName
     * @param url
     * @param userName
     * @param password
     * @param repos
     * @throws SQLException
     */
    void insertRepository(String profileName, String url, String userName, String password, String[] repos) throws SQLException;

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

    JsonObject selectRepositories() throws SQLException;
}
