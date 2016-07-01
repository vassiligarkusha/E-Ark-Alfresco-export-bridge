package dk.magenta.eark.erms;

public interface DatabaseConnectionStrategy {
  /**
   * Insert new repository into DB
   * 
   * @param profileName
   * @param url
   * @param userName
   * @param password
   * @return true is insert operation was successful and false otherwise
   */
  public boolean insertRepository(String profileName, String url, String userName, String password);
}
