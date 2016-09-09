package dk.magenta.eark.erms.ead;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObjectTypeMap {
	
	private Map<String, String> semanticTypeToCmisType;
	private Map<String, String> cmisTypeToSemanticType;
	private Map<String, Boolean> leafMap;
	
	public ObjectTypeMap() {
		semanticTypeToCmisType = new HashMap<String, String>();
		cmisTypeToSemanticType = new HashMap<String, String>();
		leafMap = new HashMap<String, Boolean>();
	}
	
	public void addObjectType(String semanticType, String cmisType, boolean leaf) {
		semanticTypeToCmisType.put(semanticType, cmisType);
		cmisTypeToSemanticType.put(cmisType, semanticType);
		leafMap.put(semanticType, leaf);
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
	
	public boolean isLeaf(String semanticType) {
		return leafMap.get(semanticType);
	}
	
	@Override
	public String toString() {
		return semanticTypeToCmisType.toString();
	}
}
