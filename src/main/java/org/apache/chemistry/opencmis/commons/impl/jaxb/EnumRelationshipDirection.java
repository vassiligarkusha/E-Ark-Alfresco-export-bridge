
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumRelationshipDirection.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumRelationshipDirection"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="source"/&gt;
 *     &lt;enumeration value="target"/&gt;
 *     &lt;enumeration value="either"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumRelationshipDirection", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumRelationshipDirection {

    @XmlEnumValue("source")
    SOURCE("source"),
    @XmlEnumValue("target")
    TARGET("target"),
    @XmlEnumValue("either")
    EITHER("either");
    private final String value;

    EnumRelationshipDirection(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumRelationshipDirection fromValue(String v) {
        for (EnumRelationshipDirection c: EnumRelationshipDirection.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
