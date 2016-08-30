package dk.magenta.eark.erms;

import dk.magenta.eark.erms.Profiles.Profile;
import dk.magenta.eark.erms.db.connector.tables.Mappings;
import dk.magenta.eark.erms.db.connector.tables.Profiles;
import dk.magenta.eark.erms.db.connector.tables.Repositories;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import dk.magenta.eark.erms.mappings.Mapping;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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

    public JDBCConnectionStrategy(PropertiesHandler propertiesHandler) throws SQLException {
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

    @Override
    public void insertRepository(String profileName, String url, String userName, String password, String[] repos) throws SQLException {

        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            List<Query> queries = new ArrayList<>();
            //First create the query for the profile table
            Query profQuery = db.insertInto(Profiles.PROFILES, Profiles.PROFILES.NAME, Profiles.PROFILES.URL,
                    Profiles.PROFILES.USERNAME, Profiles.PROFILES.PASSWORD)
                    .values(profileName, url, userName, password);
            //add it to the list
            queries.add(profQuery);
            //check if the profile has repository roots, then add queries to the list
            if (repos.length > 0) {
                for (String repo : repos) {
                    queries.add(db.insertInto(Repositories.REPOSITORIES, Repositories.REPOSITORIES.NAME,
                            Repositories.REPOSITORIES.REPOSITORYNAME).values(profileName, repo));
                }
            }
            //execute said queries
            db.batch(queries).execute();

        } catch (Exception ge) {
            ge.printStackTrace();
            System.out.println("There was an error in attempting to create the profile:\n" + ge.getMessage());
        }
    }

    @Override
    public JsonObject selectRepositories() throws SQLException {

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            List<Profile> storedProfile = db.select().from(Profiles.PROFILES)
                    .fetch()
                    .stream()
                    .map(this::convertToProfile)
                    .collect(Collectors.toList());
            storedProfile.forEach(t -> jsonArrayBuilder.add(t.toJsonObject()));

        }
        jsonObjectBuilder.add("profiles", jsonArrayBuilder);
        jsonObjectBuilder.add(Constants.SUCCESS, true);

        close();
        return jsonObjectBuilder.build();
    }

    //TODO: Switch several of the statements to use transactions and handle transaction errors properly

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
                    .filter(t -> t.getValue(Profiles.PROFILES.NAME).equalsIgnoreCase(profile.getName()))
                    .limit(1) //limit it to just the one result
                    .map(this::convertToProfile)
                    .collect(Collectors.toList());

            //Update the profile in question. This should actually lock the record during update. I think/hope
            db.update(Profiles.PROFILES)
                    .set(Profiles.PROFILES.URL, profile.getUrl())
                    .set(Profiles.PROFILES.USERNAME, profile.getUserName())
                    .set(Profiles.PROFILES.PASSWORD, profile.getPassword())
                    .where(Profiles.PROFILES.NAME.equalIgnoreCase(profile.getName()))
                    .execute();

        } catch (Exception ge) {
            System.out.println("There was an error in attempting to update the profile:\n\t\t\t" + ge.getMessage());
        } finally {
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
    public Profile getProfile(String profileName) throws SQLException {
        Profile prf = new Profile();
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            //Written just to understand how to chain the result from Jooq to a J8 stream. Actually does nothing
            prf = db.select().from(Profiles.PROFILES)
                    // LEFT JOIN expression and predicates here:
                    .leftJoin(Repositories.REPOSITORIES)
                    .on(Profiles.PROFILES.NAME.equal(Repositories.REPOSITORIES.NAME))
                    .where(Profiles.PROFILES.NAME.equalIgnoreCase(profileName))
                    .limit(1) //limit it to just the one result
                    .fetch().stream()
                    .map(this::convertToProfile)
                    .collect(Collectors.toList()).get(0);
        } catch (Exception ge) {
            ge.printStackTrace();
            System.out.println("There was an error in attempting to retrieve the profile:\n\t\t\t" + ge.getMessage());
        } finally {
            close();
        }
        return prf;
    }

    /**
     * returns whether the repository root exists for that profile
     * @param profileName the name of the profile for which we want to check
     * @param repositoryRoot the repository root name
     * @return
     * @throws SQLException
     */
    @Override
    public boolean repositoryRootExists(String profileName, String repositoryRoot) throws SQLException{
        boolean exists = false;
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            //Written just to understand how to chain the result from Jooq to a J8 stream. Actually does nothing
            Record repoRoot = db.select().from(Repositories.REPOSITORIES)
                    .where(Repositories.REPOSITORIES.NAME.equalIgnoreCase(profileName))
                    .and(Repositories.REPOSITORIES.REPOSITORYNAME.equalIgnoreCase(repositoryRoot))
                    .fetchOne();
            if(repoRoot.size() > 0)
                exists = true;

        } catch (Exception ge) {
            ge.printStackTrace();
            System.out.println("There was an error in attempting to retrieving repository: "+ repositoryRoot+
                    " from "+profileName+" profile");
        } finally {
            close();
        }
        return exists;
    }

    /**
     * Add a repository root from a profile
     *
     * @param profileName    the profile name for which we wish to add a repository root
     * @param repositoryRoot the name of the repository root we wish to add
     * @return
     * @throws SQLException
     */
    @Override
    public boolean addRepoRoot(String profileName, String repositoryRoot) throws SQLException {
        boolean created = false;
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            //Written just to understand how to chain the result from Jooq to a J8 stream. Actually does nothing
            int result = db.insertInto(Repositories.REPOSITORIES,
                    Repositories.REPOSITORIES.NAME, Repositories.REPOSITORIES.REPOSITORYNAME)
                    .values(profileName, repositoryRoot)
                    .execute();
            if(result > 0)
                created = true;
        } catch (DataAccessException dae) {
            dae.printStackTrace();
            System.out.println("Unable to add repository root due to: "+ dae.getMessage());
        } finally {
            close();
        }
        return created;
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
        close();
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

    /**
     * Remove a repository root from a profile
     *
     * @param profileName    the profile name for which we wish to subtract a repository root
     * @param repositoryRoot the name of the repository root we wish to remove
     * @return
     * @throws SQLException
     */
    @Override
    public boolean removeRepoRoot(String profileName, String repositoryRoot) throws SQLException {
        boolean deleted = false;
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            //Written just to understand how to chain the result from Jooq to a J8 stream. Actually does nothing
            int result = db.delete(Repositories.REPOSITORIES)
                    .where(Repositories.REPOSITORIES.NAME.equalIgnoreCase(profileName))
                    .and(Repositories.REPOSITORIES.REPOSITORYNAME.equalIgnoreCase(repositoryRoot))
                    .execute();
            if(result > 0)
                deleted = true;
            if (result > 1){
                logger.warn("***** WARNING!! *****\n" +
                        "More than 1 record(s) were deleted for: "+profileName+" => "+repositoryRoot);
            }
        } catch (DataAccessException dae) {
            dae.printStackTrace();
            System.out.println("Unable to remove repository root due to: "+ dae.getMessage());
        } finally {
            close();
        }
        return deleted;
    }

//private methods
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

    /**
     * Converts a single record to a profile
     *
     * @param r a single record from from the db
     * @return
     */
    private Profile convertToProfile(Record r) {
        System.out.println("Debugger");
        Profile tmp = r.into(Profile.class);
        try (DSLContext db = DSL.using(connection, SQLDialect.MYSQL)) {
            List<String> repos = db.selectFrom(Repositories.REPOSITORIES)
                    .where(Repositories.REPOSITORIES.NAME.equal(tmp.getName()))
                    .fetch()
                    .stream().map(t -> t.getValue(Repositories.REPOSITORIES.REPOSITORYNAME))
                    .collect(Collectors.toList());
            if (!repos.isEmpty())
                tmp.setRepositories(repos);
        } catch (Exception ge) {
            ge.printStackTrace();
            System.out.println("There was an error in attempting to retrieve the list of repositories:\n" + ge.getMessage());
        }
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

