package dk.magenta.eark.erms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class JDBCConnectionStrategy implements DatabaseConnectionStrategy {

  private PropertiesHandler propertiesHandler;

  private Connection connection;
  private PreparedStatement statement;
  private ResultSet rs;

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

    connection = DriverManager.getConnection(propertiesHandler.getProperty("host"),
      propertiesHandler.getProperty("userName"), propertiesHandler.getProperty("password"));
    String insertSql = "INSERT INTO Profiles VALUES (?, ?, ?, ?)";
    statement = connection.prepareStatement(insertSql);
    statement.setString(1, profileName);
    statement.setString(2, url);
    statement.setString(3, userName);
    statement.setString(4, password); // TODO: this should NOT be clear text
    statement.executeUpdate();

    close();
  }

  @Override
  public JsonObject selectRepositories() throws SQLException {

    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

    connection = DriverManager.getConnection(propertiesHandler.getProperty("host"),
      propertiesHandler.getProperty("userName"), propertiesHandler.getProperty("password"));
    String selectSql = "SELECT * from Profiles";
    statement = connection.prepareStatement(selectSql);
    rs = statement.executeQuery();
    while (rs.next()) {
      JsonObjectBuilder profile = Json.createObjectBuilder();
      profile.add(Profile.PROFILENAME, rs.getString(Profile.PROFILENAME));
      profile.add(Profile.URL, rs.getString(Profile.URL));
      profile.add(Profile.USERNAME, rs.getString(Profile.USERNAME));
      profile.add(Profile.PASSWORD, rs.getString(Profile.PASSWORD));
      jsonArrayBuilder.add(profile);
    }

    jsonObjectBuilder.add("profiles", jsonArrayBuilder);
    jsonObjectBuilder.add(Constants.SUCCESS, true);

    close();
    return jsonObjectBuilder.build();
  }

  private void close() throws SQLException {
    if (rs != null) {
      rs.close();
    }
    if (statement != null) {
      statement.close();
    }
    if (connection != null) {
      connection.close();
    }
  }
}
