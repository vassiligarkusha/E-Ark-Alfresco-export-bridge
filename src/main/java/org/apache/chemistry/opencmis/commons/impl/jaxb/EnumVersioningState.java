
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumVersioningState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumVersioningState"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="checkedout"/&gt;
 *     &lt;enumeration value="minor"/&gt;
 *     &lt;enumeration value="major"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumVersioningState", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumVersioningState {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("checkedout")
    CHECKEDOUT("checkedout"),
    @XmlEnumValue("minor")
    MINOR("minor"),
    @XmlEnumValue("major")
    MAJOR("major");
    private final String value;

    EnumVersioningState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumVersioningState fromValue(String v) {
        for (EnumVersioningState c: EnumVersioningState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
