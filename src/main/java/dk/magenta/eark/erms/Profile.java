package dk.magenta.eark.erms;

public class Profile {
  public static final String PROFILENAME = "profileName";
  public static final String URL = "url";
  public static final String USERNAME = "userName";
  public static final String PASSWORD = "password";

  private String name, url, userName, password;

  public Profile(String name, String url, String userName, String password) {
    this.name = name;
    this.url = url;
    this.userName = userName;
    this.password = password;
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
  //</editor-fold>

  /**
   * Since the profile name is mandatory and (supposedly) unique, we will only compare on that (Not allowed changeable
   * once created.
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
