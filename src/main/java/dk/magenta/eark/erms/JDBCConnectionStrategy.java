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

    Connection connection = DriverManager.getConnection(propertiesHandler.getProperty("host"),
      propertiesHandler.getProperty("userName"), propertiesHandler.getProperty("password"));
    String insertSql = "INSERT INTO Profiles VALUES (?, ?, ?, ?)";
    PreparedStatement statement = connection.prepareStatement(insertSql);
    statement.setString(1, profileName);
    statement.setString(2, url);
    statement.setString(3, userName);
    statement.setString(4, password); // TODO: this should NOT be clear text
    statement.executeUpdate();
  }

  @Override
  public JsonObject selectRepositories() throws SQLException {

    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

    Connection connection = DriverManager.getConnection(propertiesHandler.getProperty("host"),
      propertiesHandler.getProperty("userName"), propertiesHandler.getProperty("password"));
    String selectSql = "SELECT * from Profiles";
    PreparedStatement statement = connection.prepareStatement(selectSql);
    ResultSet rs = statement.executeQuery();
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

    return jsonObjectBuilder.build();
  }
}
