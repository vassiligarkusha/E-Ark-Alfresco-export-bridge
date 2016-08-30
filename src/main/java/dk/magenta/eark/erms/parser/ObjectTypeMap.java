package dk.magenta.eark.erms.parser;

import java.util.HashMap;
import java.util.Map;

public class ObjectTypeMap {
	
	private Map<String, String> repositoryTypeToCmisType;
	private Map<String, String> cmisTypeToRepositoryType;
	
	public ObjectTypeMap() {
		repositoryTypeToCmisType = new HashMap<String, String>();
		cmisTypeToRepositoryType = new HashMap<String, String>();
	}
	
	public void addObjectType(String repoType, String cmisType) {
		repositoryTypeToCmisType.put(repoType, cmisType);
		cmisTypeToRepositoryType.put(cmisType, repoType);
	}
	
	public String getRepositoryTypeFromCmisType(String cmisType) {
		return cmisTypeToRepositoryType.get(cmisType);
	}
	
	public String getCmisTypeFromRepositoryType(String repoType) {
		return repositoryTypeToCmisType.get(repoType);
	}
	
	@Override
	public String toString() {
		return repositoryTypeToCmisType.toString();
	}
	// public List getAll...()
}
