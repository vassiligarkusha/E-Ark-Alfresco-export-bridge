package dk.magenta.eark.erms.ead;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Hook {

	private String xpath;
	private String cmisPropertyId;
	private List<Pair<String, String>> escapes; 
	
	public Hook(String xpath, String cmisPropertyId) {
		this.xpath = xpath;
		this.cmisPropertyId = cmisPropertyId;
	}
	
	public void addEscapeHook(String regex, String replacement) {
		if (escapes == null) {
			escapes = new LinkedList<Pair<String,String>>();
		}
		Pair<String, String> escape = new ImmutablePair<String, String>(regex, replacement);
		escapes.add(escape);
	}
	
	public List<Pair<String, String>> getEscapes() {
		return escapes;
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
