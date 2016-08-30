package dk.magenta.eark.erms.db;

import dk.magenta.eark.erms.db.connector.tables.Mappings;
import dk.magenta.eark.erms.db.connector.tables.Profiles;
import dk.magenta.eark.erms.db.connector.tables.Repositories;
import dk.magenta.eark.erms.mappings.Mapping;
import dk.magenta.eark.erms.repository.profiles.Profile;
import dk.magenta.eark.erms.system.PropertiesHandler;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JDBCConnectionStrategy implements DatabaseConnectionStrategy {
    private final Logger logger = LoggerFactory.getLogger(JDBCConnectionStrategy.class);
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

    //TODO: Switch several of the statements to use transactions and handle transaction errors properly

    @Override
    public void insertRepository(String name, String url, String userName, String password) throws SQLException {

        String insertSql = "INSERT INTO Profiles VALUES (?, ?, ?, ?)";
        statement = connection.prepareStatement(insertSql);
        statement.setString(1, name);
        statement.setString(2, url);
        statement.setString(3, userName);
        statement.setString(4, password); // TODO: this should NOT be clear text
        statement.executeUpdate();
        close();
    }

    /**
     * Returns a list of profiles from the db
     * @return
     * @throws SQLException
     */
    public List<Profile> getProfiles() throws SQLException {
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            return db.select().from(Profiles.PROFILES)
                    .fetch()
                    .stream()
                    .map(this::convertToProfile)
                    .collect(Collectors.toList());
        }
        catch (Exception ge){
            ge.printStackTrace();
        }
        finally{
            close();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Adds the profile to the db
     *
     * @param profile
     * @return true | false
     * @throws SQLException
     */
    @Override
    public boolean createProfile(Profile profile) throws SQLException {

        int result;
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            List<Query> queries = new ArrayList<>();
            //First create the query for the profile table
            Query profQuery = db.insertInto(Profiles.PROFILES, Profiles.PROFILES.NAME, Profiles.PROFILES.URL,
                    Profiles.PROFILES.USERNAME, Profiles.PROFILES.PASSWORD)
                    .values(profile.getName(), profile.getUrl(), profile.getUserName(), profile.getPassword());

            //add it to the list
            queries.add(profQuery);

            //check if the profile has repository roots, then add queries to the list
            if (profile.getRepositories().size() > 0) {
                queries.addAll(profile.getRepositories().stream().map(repo -> db.insertInto(Repositories.REPOSITORIES,
                        Repositories.REPOSITORIES.NAME, Repositories.REPOSITORIES.REPOSITORYNAME)
                        .values(profile.getName(), repo)).collect(Collectors.toList()));
            }

            result = db.transactionResult( configuration -> {
                int[] commits = DSL.using(configuration).batch(queries).execute();

                if(commits.length != queries.size()) {
                    logger.error("The data may not have been persisted correctly."+ commits +
                            ": number of queries were executed instead of "+queries.size()+" queries");
                }
                return commits.length;

            });
        } catch (Exception ge){
            ge.printStackTrace();
            logger.error("An issue with persisting the profile to the db.\n"+ ge.getMessage());
            return false;
        }
        return result <= 1;
    }

    /**
     * Removes a single profile from the repository
     * @param profileName the name of the profile to remove
     * @return
     * @throws SQLException
     */
    @Override
    public boolean deleteProfile(String profileName) throws SQLException {
        int result;
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            result = db.transactionResult( configuration -> {
                int deletes = DSL.using(configuration).delete(Profiles.PROFILES)
                        .where(Profiles.PROFILES.NAME.equalIgnoreCase(profileName))
                        .execute();

                if(deletes >= 1) {
                    logger.error("The number of deletions made were ["+ (deletes - 1) +"] more than was necessary");
                }
                if(deletes < 1) {
                    logger.error("No deletions were performed. DB transaction result: "+ deletes);
                }
                return deletes;
            });
        } catch (Exception ge){
            ge.printStackTrace();
            logger.error("An issue removing the profile from the db.\n"+ ge.getMessage());
            return false;
        }
        return result==1;
    }

    /**
     * Updates a profile in the db
     *
     * @param profile
     * @throws SQLException
     */
    @Override
    public boolean updateProfile(Profile profile) throws SQLException {
        int result;
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            result = db.transactionResult( configuration -> {
                //Update the profile in question. This should actually lock the record during update. I think/hope
               int commits = DSL.using(configuration).update(Profiles.PROFILES)
                        .set(Profiles.PROFILES.URL, profile.getUrl())
                        .set(Profiles.PROFILES.USERNAME, profile.getUserName())
                        .set(Profiles.PROFILES.PASSWORD, profile.getPassword())
                        .where(Profiles.PROFILES.NAME.equalIgnoreCase(profile.getName()))
                        .execute();
               if(commits != 1) {
                   logger.warn("The update may not have been done correctly. Output of the result was: " + commits);
               }
               return commits;
            });

        } catch (Exception ge) {
            System.out.println("There was an error in attempting to update the profile:\n\t\t\t" + ge.getMessage());
            return false;
        }
        finally {
            close();
        }
        return result==1;
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
                    .filter(t -> t.getValue(Profiles.PROFILES.NAME).equalsIgnoreCase(profileName))
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
     * Removes a mapping from db given the system mapping name
     *
     * @param mappingName the name of the mapping to delete
     * @return a boolean indicating success
     * @throws SQLException
     */
    @Override
    public boolean deleteMapping(String mappingName) throws SQLException {
        int result;
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            result = db.transactionResult( configuration -> {
                int deletes = DSL.using(configuration).delete(Mappings.MAPPINGS)
                        .where(Mappings.MAPPINGS.NAME.equalIgnoreCase(mappingName))
                        .execute();

                if(deletes != 1) {
                    System.out.println("Grave error in deletion attempt. Number of deleted records were: "+ deletes);
                    logger.error("The number of deletions made was ["+ (deletes - 1) +"] more than was necessary");
                }
                return deletes;
            });
        } catch (Exception ge){
            ge.printStackTrace();
            logger.error("An issue removing the mapping from the db.\n"+ ge.getMessage());
            return false;
        }
        return result==1;
    }

    /**
     * Gets a mapping from db given the system mapping name
     *
     * @param mappingName the name of the mapping to retrieve
     * @return json object representing the mapping
     * @throws SQLException
     */
    @Override
    public Mapping getMapping(String mappingName) throws SQLException {
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            Record mapping = db.select().from(Mappings.MAPPINGS)
                    .where(Mappings.MAPPINGS.NAME.equalIgnoreCase(mappingName))
                    .fetchOne();
            return convertToMapping(mapping);
        }
        catch (Exception ge){
            ge.printStackTrace();
        }
        close();
        return Mapping.EMPTY_MAP;
    }

    /**
     * Gets all mappings on the system
     *
     * @return
     */
    @Override
    public List getMappings() throws SQLException{
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            return db.select().from(Mappings.MAPPINGS)
                    .fetch()
                    .stream()
                    .map(this::convertToMapping)
                    .collect(Collectors.toList());
        }
        catch (Exception ge){
            ge.printStackTrace();
        }
        finally {
            close();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Persists the information about the saved file into the db
     * @param mapName the name with which to give the mapping file
     * @param sysPath the location of the actual mapping file on disk
     * @return
     */
    @Override
    public boolean saveMapping(String mapName,String sysPath, FormDataContentDisposition fileMetadata){
        int result;

        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
             result = db.transactionResult( configuration -> {
                int commits = DSL.using(configuration).insertInto(Mappings.MAPPINGS, Mappings.MAPPINGS.NAME,
                        Mappings.MAPPINGS.SYSPATH, Mappings.MAPPINGS.CREATED, Mappings.MAPPINGS.REALFILENAME)
                        .values(mapName, sysPath, new Date(Calendar.getInstance().getTime().getTime()),
                                fileMetadata.getFileName() )
                        .execute();

                if(commits != 1) {
                    System.out.println("The data may not have been persisted correctly. Output of the result was: "+ commits);
                    logger.error("The data may not have been persisted correctly. Output of the result was: "+ commits);
                }
                return commits;

            });
        } catch (Exception ge){
            ge.printStackTrace();
            logger.error("An issue with persisting the mapping in the db.\n"+ ge.getMessage());
            return false;
        }
        return result==1;
    }

//private methods
    /**
     * Converts a single record to a profile
     * @param r a single from from the db
     * @return
     */
    private Profile convertToProfile(Record r){
        return new Profile(r.getValue(Profiles.PROFILES.NAME), r.getValue(Profiles.PROFILES.URL),
                r.getValue(Profiles.PROFILES.USERNAME), r.getValue(Profiles.PROFILES.PASSWORD));
    }

    /**
     * Converts a single record to a mapping
     *
     * @param r a single mapping from from the db
     * @return
     */
    private Mapping convertToMapping(Record r) {
        System.out.println("Debugger");
        Mapping tmp = r.into(Mapping.class);
        return tmp;
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
