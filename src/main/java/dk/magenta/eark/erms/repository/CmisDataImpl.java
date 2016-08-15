package dk.magenta.eark.erms.repository;

public class CmisDataImpl implements CmisData {

	private String objectId;
	private String objectType;
	
	public CmisDataImpl(String objectId, String objectType) {
		this.objectId = objectId;
		this.objectType = objectType;
	}
	
	@Override
	public String getObjectId() {
		return objectId;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}
	
}
