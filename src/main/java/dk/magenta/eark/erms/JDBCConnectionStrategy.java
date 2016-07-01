package dk.magenta.eark.erms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCConnectionStrategy implements DatabaseConnectionStrategy {

  @Override
  public boolean insertRepository(String profileName, String url, String userName, String password) {

    // Will be removed - just doing a few tests

    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/world", "andreas", "hemmeligt");
      PreparedStatement statement = connection.prepareStatement("SELECT * FROM City");
      ResultSet r = statement.executeQuery();
      while (r.next()) {
        System.out.println(r.getString(2));
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return false;
  }

}
