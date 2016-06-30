package dk.magenta.eark.erms;

import dk.magenta.eark.erms.db.connector.tables.Profiles;
import dk.magenta.eark.erms.db.connector.tables.Repositories;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author lanre.
 */
public class LoadProfile {
    public void getInfo() throws SQLException {
        Connection dbConn = getConnection();
        DSLContext db = DSL.using(dbConn, SQLDialect.MYSQL);
        getProfiles(db);
    }

    private void getProfiles (DSLContext db){
        final String ADMIN_USERNAME = "admin";
        Result<?> dbResult = db.select(Profiles.PROFILES.NAME, Repositories.REPOSITORIES.URL).from(Profiles.PROFILES)
                .where(Profiles.PROFILES.USERNAME.eq(ADMIN_USERNAME)).fetch();

        System.out.println("The resulting result from the db: "+ dbResult.format());
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/exm",
                    "emt",
                    "password");
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
