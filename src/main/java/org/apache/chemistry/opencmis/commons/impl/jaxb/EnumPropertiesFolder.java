
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumPropertiesFolder.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumPropertiesFolder"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="cmis:parentId"/&gt;
 *     &lt;enumeration value="cmis:allowedChildObjectTypeIds"/&gt;
 *     &lt;enumeration value="cmis:path"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumPropertiesFolder", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumPropertiesFolder {

    @XmlEnumValue("cmis:parentId")
    CMIS_PARENT_ID("cmis:parentId"),
    @XmlEnumValue("cmis:allowedChildObjectTypeIds")
    CMIS_ALLOWED_CHILD_OBJECT_TYPE_IDS("cmis:allowedChildObjectTypeIds"),
    @XmlEnumValue("cmis:path")
    CMIS_PATH("cmis:path");
    private final String value;

    EnumPropertiesFolder(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumPropertiesFolder fromValue(String v) {
        for (EnumPropertiesFolder c: EnumPropertiesFolder.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
