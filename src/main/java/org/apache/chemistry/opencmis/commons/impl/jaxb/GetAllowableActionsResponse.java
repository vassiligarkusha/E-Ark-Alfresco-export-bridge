
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
 *         &lt;element name="allowableActions" type="{http://docs.oasis-open.org/ns/cmis/core/200908/}cmisAllowableActionsType"/&gt;
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
    "allowableActions"
})
@XmlRootElement(name = "getAllowableActionsResponse")
public class GetAllowableActionsResponse {

    @XmlElement(required = true)
    protected CmisAllowableActionsType allowableActions;

    /**
     * Gets the value of the allowableActions property.
     * 
     * @return
     *     possible object is
     *     {@link CmisAllowableActionsType }
     *     
     */
    public CmisAllowableActionsType getAllowableActions() {
        return allowableActions;
    }

    /**
     * Sets the value of the allowableActions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CmisAllowableActionsType }
     *     
     */
    public void setAllowableActions(CmisAllowableActionsType value) {
        this.allowableActions = value;
    }

}
