
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="objects" type="{http://docs.oasis-open.org/ns/cmis/messaging/200908/}cmisObjectListType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "objects"
})
@XmlRootElement(name = "getCheckedOutDocsResponse")
public class GetCheckedOutDocsResponse {

    @XmlElement(required = true)
    protected CmisObjectListType objects;

    /**
     * Gets the value of the objects property.
     * 
     * @return
     *     possible object is
     *     {@link CmisObjectListType }
     *     
     */
    public CmisObjectListType getObjects() {
        return objects;
    }

    /**
     * Sets the value of the objects property.
     * 
     * @param value
     *     allowed object is
     *     {@link CmisObjectListType }
     *     
     */
    public void setObjects(CmisObjectListType value) {
        this.objects = value;
    }

}
