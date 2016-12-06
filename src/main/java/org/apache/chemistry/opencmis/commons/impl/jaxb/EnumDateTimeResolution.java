
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumDateTimeResolution.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumDateTimeResolution"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="year"/&gt;
 *     &lt;enumeration value="date"/&gt;
 *     &lt;enumeration value="time"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumDateTimeResolution", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumDateTimeResolution {

    @XmlEnumValue("year")
    YEAR("year"),
    @XmlEnumValue("date")
    DATE("date"),
    @XmlEnumValue("time")
    TIME("time");
    private final String value;

    EnumDateTimeResolution(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumDateTimeResolution fromValue(String v) {
        for (EnumDateTimeResolution c: EnumDateTimeResolution.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
