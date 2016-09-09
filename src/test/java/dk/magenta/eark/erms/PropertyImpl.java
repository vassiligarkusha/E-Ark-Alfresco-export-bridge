package dk.magenta.eark.erms;

import java.util.List;

import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;

public class PropertyImpl<T> implements Property<T> {

	private String value;
	
	public PropertyImpl(String value) {
		this.value = value;
	}
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getFirstValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CmisExtensionElement> getExtensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExtensions(List<CmisExtensionElement> extensions) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isMultiValued() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PropertyType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyDefinition<T> getDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U> U getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValueAsString() {
		return value;
	}

	@Override
	public String getValuesAsString() {
		return value;
	}

}
