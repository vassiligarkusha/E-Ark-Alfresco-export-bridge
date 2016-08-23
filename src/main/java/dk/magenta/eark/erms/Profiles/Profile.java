package dk.magenta.eark.erms.Profiles;

import org.apache.commons.lang3.StringUtils;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;

public class Profile {
    public static final String NAME = "name";
    public static final String FOLDER_OBJECT_ID = "folderObjectId";
    public static final String DOCUMENT_OBJECT_ID = "documentObjectId";
    public static final String URL = "url";
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";
    public static final String ROOT_REPOSITORIES = "repositories";

    private String name, url, userName, password;
    List<String>repositories;

    /**
     * Empty constructor meaning
     */
    public Profile() {
    }

    public Profile(String name, String url, String userName, String password) {
        this.name = name;
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    public Profile(String name, String url, String userName, String password, List<String> repositories) {
        this.name = name;
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.repositories = repositories;
    }

    //<editor-fold desc="Getter methods">
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRepositories() {
        return repositories;
    }
    //</editor-fold
    //repository(ies) setter
    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }

    /**
     * Returns a json representation of an instance of this object
     * @return
     */
    public JsonObject toJsonObject(){
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("name", this.getName());
        jsonObjectBuilder.add("url", this.getUrl());
        jsonObjectBuilder.add("userName", this.getUserName());
        jsonObjectBuilder.add("password", this.getPassword());
        if(this.repositories!= null && !emptyRepos()) {
            JsonArrayBuilder repos = Json.createArrayBuilder();
            this.getRepositories().forEach(repos::add);
            jsonObjectBuilder.add("repositories", repos);
        }
        return jsonObjectBuilder.build();
    }

    /**
     * Tests to see if this is an empty profile. Returns true if the profile name is empty
     * We only test for this since it is the only mandatory property.
     *
     * @return true|false
     */
    public boolean isEmpty(){
        return StringUtils.isBlank(this.name);
    }

    /**
     * Just says whether this profile has an empty repo list
     * @return
     */
    public boolean emptyRepos(){return this.repositories.isEmpty();}

    /**
     * Since the profile name is mandatory and (supposedly) unique, we will only compare on that (Not allowed changeable
     * once created.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;

        Profile profile = (Profile) o;

        return getName().equals(profile.getName());

    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
