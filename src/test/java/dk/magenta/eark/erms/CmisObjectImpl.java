package dk.magenta.eark.erms;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.SecondaryType;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;

public class CmisObjectImpl<T> implements CmisObject {

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Property<?>> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Property<T> getProperty(String id) {
		Property<T> property = new PropertyImpl<T>("hurra");
		return property;
	}

	@Override
	public <T> T getPropertyValue(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreatedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GregorianCalendar getCreationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastModifiedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GregorianCalendar getLastModificationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseTypeId getBaseTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectType getBaseType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SecondaryType> getSecondaryTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObjectType> findObjectType(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getChangeToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AllowableActions getAllowableActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAllowableAction(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Relationship> getRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Acl getAcl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getPermissionsForPrincipal(String principalId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getPermissonsForPrincipal(String principalId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(boolean allVersions) {
		// TODO Auto-generated method stub

	}

	@Override
	public CmisObject updateProperties(Map<String, ?> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectId updateProperties(Map<String, ?> properties, boolean refresh) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CmisObject rename(String newName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectId rename(String newName, boolean refresh) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Rendition> getRenditions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyPolicy(ObjectId... policyIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyPolicy(ObjectId policyId, boolean refresh) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePolicy(ObjectId... policyIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePolicy(ObjectId policyId, boolean refresh) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Policy> getPolicies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Acl applyAcl(List<Ace> addAces, List<Ace> removeAces, AclPropagation aclPropagation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Acl addAcl(List<Ace> addAces, AclPropagation aclPropagation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Acl removeAcl(List<Ace> removeAces, AclPropagation aclPropagation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Acl setAcl(List<Ace> aces) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CmisExtensionElement> getExtensions(ExtensionLevel level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getAdapter(Class<T> adapterInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getRefreshTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshIfOld(long durationInMillis) {
		// TODO Auto-generated method stub

	}

}
