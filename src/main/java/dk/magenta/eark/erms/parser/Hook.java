package dk.magenta.eark.erms.parser;

public class Hook {

	private String path;
	private String cmisProperty;
	private String attribute;
	
	public Hook(String path, String cmisProperty) {
		this.path = path;
		this.cmisProperty = cmisProperty;
	}
	
	public Hook(String path, String cmisProperty, String attribute) {
		this(path, cmisProperty);
		this.attribute = attribute;
	}
	
	public boolean isAttribute() {
		if (attribute == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "{" + path + ", " + cmisProperty + ", " + attribute + "}";
	}
}
