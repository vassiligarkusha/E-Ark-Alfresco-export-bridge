
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumUnfileObject.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumUnfileObject"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="unfile"/&gt;
 *     &lt;enumeration value="deletesinglefiled"/&gt;
 *     &lt;enumeration value="delete"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumUnfileObject", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumUnfileObject {

    @XmlEnumValue("unfile")
    UNFILE("unfile"),
    @XmlEnumValue("deletesinglefiled")
    DELETESINGLEFILED("deletesinglefiled"),
    @XmlEnumValue("delete")
    DELETE("delete");
    private final String value;

    EnumUnfileObject(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumUnfileObject fromValue(String v) {
        for (EnumUnfileObject c: EnumUnfileObject.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
