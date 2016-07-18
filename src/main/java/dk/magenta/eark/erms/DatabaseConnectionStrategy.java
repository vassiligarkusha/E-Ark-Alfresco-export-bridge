package dk.magenta.eark.erms;

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
  public void insertRepository(String profileName, String url, String userName, String password) throws SQLException;

  /**
   * Updates a profile in the db
   * @param profile
   * @throws SQLException
   */
  public void updateProfile(Profile profile) throws SQLException;

  public JsonObject selectRepositories() throws SQLException;
}