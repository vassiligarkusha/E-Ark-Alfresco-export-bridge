
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumSupportedPermissions.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumSupportedPermissions"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="basic"/&gt;
 *     &lt;enumeration value="repository"/&gt;
 *     &lt;enumeration value="both"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumSupportedPermissions", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumSupportedPermissions {

    @XmlEnumValue("basic")
    BASIC("basic"),
    @XmlEnumValue("repository")
    REPOSITORY("repository"),
    @XmlEnumValue("both")
    BOTH("both");
    private final String value;

    EnumSupportedPermissions(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumSupportedPermissions fromValue(String v) {
        for (EnumSupportedPermissions c: EnumSupportedPermissions.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
