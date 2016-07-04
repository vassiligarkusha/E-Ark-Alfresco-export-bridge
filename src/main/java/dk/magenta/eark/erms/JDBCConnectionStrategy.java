package dk.magenta.eark.erms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCConnectionStrategy implements DatabaseConnectionStrategy {

  private PropertiesHandler propertiesHandler;

  public JDBCConnectionStrategy(PropertiesHandler propertiesHandler) {
    this.propertiesHandler = propertiesHandler;
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void insertRepository(String profileName, String url, String userName, String password) throws SQLException {

    Connection connection;
    connection = DriverManager.getConnection(propertiesHandler.getProperty("host"),
      propertiesHandler.getProperty("userName"), propertiesHandler.getProperty("password"));
    String insertSql = "INSERT INTO Profiles VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(insertSql);
    statement.setString(1, profileName);
    statement.setString(2, url);
    statement.setString(3, userName);
    statement.setString(4, password); // TODO: this should NOT be clear text
    statement.executeUpdate();
  }
}
