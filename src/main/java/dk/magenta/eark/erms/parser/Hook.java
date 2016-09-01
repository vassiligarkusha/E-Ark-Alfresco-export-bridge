package dk.magenta.eark.erms.parser;

// Refactor to use Pair instead
public class Hook {

	private String xpath;
	private String cmisPropertyId;
	
	public Hook(String xpath, String cmisPropertyId) {
		this.xpath = xpath;
		this.cmisPropertyId = cmisPropertyId;
	}
	
	public String getCmisPropertyId() {
		return cmisPropertyId;
	}
	
	public String getXpath() {
		return xpath;
	}
	
	@Override
	public String toString() {
		return "{" + cmisPropertyId + " -> " + xpath + "}";
	}
}
