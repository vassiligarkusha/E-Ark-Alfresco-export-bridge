package dk.magenta.eark.erms;

import dk.magenta.eark.erms.db.connector.tables.Profiles;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class JDBCConnectionStrategy implements DatabaseConnectionStrategy {

    private PropertiesHandler propertiesHandler;

    private Connection connection;
    private PreparedStatement statement;
    private ResultSet rs;

    public JDBCConnectionStrategy(PropertiesHandler propertiesHandler) throws SQLException{
        this.propertiesHandler = propertiesHandler;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String host = propertiesHandler.getProperty("host");
            String userName = propertiesHandler.getProperty("userName");
            String password = propertiesHandler.getProperty("password");
            //Create a connection
            this.connection = DriverManager.getConnection(host, userName, password);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertRepository(String profileName, String url, String userName, String password) throws SQLException {

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

    /**
     * Updates a profile in the db
     *
     * @param profile
     * @throws SQLException
     */
    @Override
    public void updateProfile(Profile profile) throws SQLException {
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            //Written just to understand how to chain the result from Jooq to a J8 stream. Actually does nothing
            List<Profile> storedProfile = db.select().from(Profiles.PROFILES).fetch().stream()
                    .filter(t -> t.getValue(Profiles.PROFILES.PROFILENAME).equalsIgnoreCase(profile.getName()))
                    .limit(1) //limit it to just the one result
                    .map(this::convertToProfile)
                    .collect(Collectors.toList());

            //Update the profile in question. This should actually lock the record during update. I think/hope
            db.update(Profiles.PROFILES)
                    .set(Profiles.PROFILES.URL, profile.getUrl())
                    .set(Profiles.PROFILES.USERNAME, profile.getUserName())
                    .set(Profiles.PROFILES.PASSWORD, profile.getPassword())
                    .where(Profiles.PROFILES.PROFILENAME.equalIgnoreCase(profile.getName()))
                    .execute();

        } catch (Exception ge) {
            System.out.println("There was an error in attempting to update the profile:\n\t\t\t" + ge.getMessage());
        }
        finally {
            close();
        }
    }

    /**
     * Gets a single profile from the db using the profile name (Profile name is unique)
     *
     * @param profileName the name of the profile to retrieve from the db
     * @return
     * @throws SQLException
     */
    @Override
    public Profile getProfile(String profileName) throws SQLException{
        Profile prf = new Profile();
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            //Written just to understand how to chain the result from Jooq to a J8 stream. Actually does nothing
            prf = db.select().from(Profiles.PROFILES).fetch().stream()
                    .filter(t -> t.getValue(Profiles.PROFILES.PROFILENAME).equalsIgnoreCase(profileName))
                    .limit(1) //limit it to just the one result
                    .map(this::convertToProfile)
                    .collect(Collectors.toList()).get(0);
        }
        catch (Exception ge) {
            System.out.println("There was an error in attempting to update the profile:\n\t\t\t" + ge.getMessage());
        }
        finally {
            close();
        }
        return prf;
    }

    /**
     * Converts a single record to a profile
     * @param r a single from from the db
     * @return
     */
    private Profile convertToProfile(Record r){
        return new Profile(r.getValue(Profiles.PROFILES.PROFILENAME), r.getValue(Profiles.PROFILES.URL),
                r.getValue(Profiles.PROFILES.USERNAME), r.getValue(Profiles.PROFILES.PASSWORD));
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
