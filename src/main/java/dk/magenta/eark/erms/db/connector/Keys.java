/**
 * This class is generated by jOOQ
 */
package dk.magenta.eark.erms.db.connector;


import dk.magenta.eark.erms.db.connector.tables.Person;
import dk.magenta.eark.erms.db.connector.tables.Profiles;
import dk.magenta.eark.erms.db.connector.tables.Repositories;
import dk.magenta.eark.erms.db.connector.tables.records.PersonRecord;
import dk.magenta.eark.erms.db.connector.tables.records.ProfilesRecord;
import dk.magenta.eark.erms.db.connector.tables.records.RepositoriesRecord;

import javax.annotation.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;
import org.jooq.types.UInteger;


/**
 * A class modelling foreign key relationships between tables of the <code>exm</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<ProfilesRecord, UInteger> IDENTITY_PROFILES = Identities0.IDENTITY_PROFILES;
    public static final Identity<RepositoriesRecord, UInteger> IDENTITY_REPOSITORIES = Identities0.IDENTITY_REPOSITORIES;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<PersonRecord> KEY_PERSON_PRIMARY = UniqueKeys0.KEY_PERSON_PRIMARY;
    public static final UniqueKey<ProfilesRecord> KEY_PROFILES_PRIMARY = UniqueKeys0.KEY_PROFILES_PRIMARY;
    public static final UniqueKey<ProfilesRecord> KEY_PROFILES_UNIQUE_INDEX = UniqueKeys0.KEY_PROFILES_UNIQUE_INDEX;
    public static final UniqueKey<RepositoriesRecord> KEY_REPOSITORIES_PRIMARY = UniqueKeys0.KEY_REPOSITORIES_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<ProfilesRecord, UInteger> IDENTITY_PROFILES = createIdentity(Profiles.PROFILES, Profiles.PROFILES.ID);
        public static Identity<RepositoriesRecord, UInteger> IDENTITY_REPOSITORIES = createIdentity(Repositories.REPOSITORIES, Repositories.REPOSITORIES.ID);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<PersonRecord> KEY_PERSON_PRIMARY = createUniqueKey(Person.PERSON, "KEY_person_PRIMARY", Person.PERSON.USERNAME);
        public static final UniqueKey<ProfilesRecord> KEY_PROFILES_PRIMARY = createUniqueKey(Profiles.PROFILES, "KEY_profiles_PRIMARY", Profiles.PROFILES.ID);
        public static final UniqueKey<ProfilesRecord> KEY_PROFILES_UNIQUE_INDEX = createUniqueKey(Profiles.PROFILES, "KEY_profiles_unique_index", Profiles.PROFILES.ID, Profiles.PROFILES.USERNAME);
        public static final UniqueKey<RepositoriesRecord> KEY_REPOSITORIES_PRIMARY = createUniqueKey(Repositories.REPOSITORIES, "KEY_repositories_PRIMARY", Repositories.REPOSITORIES.ID);
    }
}