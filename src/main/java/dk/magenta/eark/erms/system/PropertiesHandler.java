package dk.magenta.eark.erms.system;

public interface PropertiesHandler {
	public String getProperty(String key);
	public void setProperty(String key, String value);
}
