
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumServiceException.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumServiceException"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="constraint"/&gt;
 *     &lt;enumeration value="nameConstraintViolation"/&gt;
 *     &lt;enumeration value="contentAlreadyExists"/&gt;
 *     &lt;enumeration value="filterNotValid"/&gt;
 *     &lt;enumeration value="invalidArgument"/&gt;
 *     &lt;enumeration value="notSupported"/&gt;
 *     &lt;enumeration value="objectNotFound"/&gt;
 *     &lt;enumeration value="permissionDenied"/&gt;
 *     &lt;enumeration value="runtime"/&gt;
 *     &lt;enumeration value="storage"/&gt;
 *     &lt;enumeration value="streamNotSupported"/&gt;
 *     &lt;enumeration value="updateConflict"/&gt;
 *     &lt;enumeration value="versioning"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumServiceException")
@XmlEnum
public enum EnumServiceException {

    @XmlEnumValue("constraint")
    CONSTRAINT("constraint"),
    @XmlEnumValue("nameConstraintViolation")
    NAME_CONSTRAINT_VIOLATION("nameConstraintViolation"),
    @XmlEnumValue("contentAlreadyExists")
    CONTENT_ALREADY_EXISTS("contentAlreadyExists"),
    @XmlEnumValue("filterNotValid")
    FILTER_NOT_VALID("filterNotValid"),
    @XmlEnumValue("invalidArgument")
    INVALID_ARGUMENT("invalidArgument"),
    @XmlEnumValue("notSupported")
    NOT_SUPPORTED("notSupported"),
    @XmlEnumValue("objectNotFound")
    OBJECT_NOT_FOUND("objectNotFound"),
    @XmlEnumValue("permissionDenied")
    PERMISSION_DENIED("permissionDenied"),
    @XmlEnumValue("runtime")
    RUNTIME("runtime"),
    @XmlEnumValue("storage")
    STORAGE("storage"),
    @XmlEnumValue("streamNotSupported")
    STREAM_NOT_SUPPORTED("streamNotSupported"),
    @XmlEnumValue("updateConflict")
    UPDATE_CONFLICT("updateConflict"),
    @XmlEnumValue("versioning")
    VERSIONING("versioning");
    private final String value;

    EnumServiceException(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumServiceException fromValue(String v) {
        for (EnumServiceException c: EnumServiceException.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
