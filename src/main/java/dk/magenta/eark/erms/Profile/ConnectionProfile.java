package dk.magenta.eark.erms.Profile;

/**
 * @author lanre.
 */
public class ConnectionProfile {

    private String url;
    private String name;
    private String ownerId;

    public ConnectionProfile(String url, String name, String ownerId) {
        this.url = url;
        this.name = name;
        this.ownerId = ownerId;
    }

    //<editor-fold desc="Getters and Setters">
    public String getUrl() {
        return url;
    }
    public String getName() {
        return name;
    }
    public String getOwnerId() {
        return ownerId;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    //</editor-fold>

}
