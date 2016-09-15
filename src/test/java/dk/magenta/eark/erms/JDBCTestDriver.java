package dk.magenta.eark.erms;

import dk.magenta.eark.erms.db.DatabaseConnectionStrategy;
import dk.magenta.eark.erms.db.JDBCConnectionStrategy;
import dk.magenta.eark.erms.system.PropertiesHandlerImpl;

public class JDBCTestDriver {

  public static void main(String[] args) throws Exception {

    DatabaseConnectionStrategy databaseConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl(
      Constants.SETTINGS));
//    databaseConnectionStrategy.insertRepository("test", "url", "user", "pwd");

  }
}
