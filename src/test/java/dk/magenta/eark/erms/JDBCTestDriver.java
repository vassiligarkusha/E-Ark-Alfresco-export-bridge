package dk.magenta.eark.erms;


public class JDBCTestDriver {

  public static void main(String[] args) throws Exception {

    DatabaseConnectionStrategy databaseConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl(
      Constants.SETTINGS));
    databaseConnectionStrategy.insertRepository("test", "url", "user", "pwd");

  }
}
