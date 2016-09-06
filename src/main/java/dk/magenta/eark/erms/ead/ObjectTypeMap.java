package dk.magenta.eark.erms.ead;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObjectTypeMap {
	
	private Map<String, String> semanticTypeToCmisType;
	private Map<String, String> cmisTypeToSemanticType;
	
	public ObjectTypeMap() {
		semanticTypeToCmisType = new HashMap<String, String>();
		cmisTypeToSemanticType = new HashMap<String, String>();
	}
	
	public void addObjectType(String repoType, String cmisType) {
		semanticTypeToCmisType.put(repoType, cmisType);
		cmisTypeToSemanticType.put(cmisType, repoType);
	}
	
	public String getSemanticTypeFromCmisType(String cmisType) {
		return cmisTypeToSemanticType.get(cmisType);
	}
	
	public String getCmisTypeFromSemanticType(String repoType) {
		return semanticTypeToCmisType.get(repoType);
	}
	
	public Set<String> getAllSemanticTypes() {
		return semanticTypeToCmisType.keySet();
	}
	
	public Set<String> getAllCmisTypes() {
		return cmisTypeToSemanticType.keySet();
	}
	
	@Override
	public String toString() {
		return semanticTypeToCmisType.toString();
	}
}
