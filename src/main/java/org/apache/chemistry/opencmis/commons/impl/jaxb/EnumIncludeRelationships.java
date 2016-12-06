
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumIncludeRelationships.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumIncludeRelationships"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="source"/&gt;
 *     &lt;enumeration value="target"/&gt;
 *     &lt;enumeration value="both"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumIncludeRelationships", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumIncludeRelationships {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("source")
    SOURCE("source"),
    @XmlEnumValue("target")
    TARGET("target"),
    @XmlEnumValue("both")
    BOTH("both");
    private final String value;

    EnumIncludeRelationships(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumIncludeRelationships fromValue(String v) {
        for (EnumIncludeRelationships c: EnumIncludeRelationships.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
