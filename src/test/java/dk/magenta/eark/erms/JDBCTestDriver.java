package dk.magenta.eark.erms;

public class JDBCTestDriver {

  public static void main(String[] args) {

    DatabaseConnectionStrategy databaseConnectionStrategy = new JDBCConnectionStrategy();
    databaseConnectionStrategy.insertRepository(null, null, null, null);

  }

}
