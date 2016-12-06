
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumCapabilityOrderBy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumCapabilityOrderBy"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="common"/&gt;
 *     &lt;enumeration value="custom"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumCapabilityOrderBy", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumCapabilityOrderBy {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("common")
    COMMON("common"),
    @XmlEnumValue("custom")
    CUSTOM("custom");
    private final String value;

    EnumCapabilityOrderBy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumCapabilityOrderBy fromValue(String v) {
        for (EnumCapabilityOrderBy c: EnumCapabilityOrderBy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
