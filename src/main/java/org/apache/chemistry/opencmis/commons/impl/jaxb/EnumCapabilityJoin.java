
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumCapabilityJoin.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumCapabilityJoin"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="inneronly"/&gt;
 *     &lt;enumeration value="innerandouter"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumCapabilityJoin", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumCapabilityJoin {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("inneronly")
    INNERONLY("inneronly"),
    @XmlEnumValue("innerandouter")
    INNERANDOUTER("innerandouter");
    private final String value;

    EnumCapabilityJoin(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumCapabilityJoin fromValue(String v) {
        for (EnumCapabilityJoin c: EnumCapabilityJoin.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
