
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmisTypeDocumentDefinitionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmisTypeDocumentDefinitionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://docs.oasis-open.org/ns/cmis/core/200908/}cmisTypeDefinitionType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="versionable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="contentStreamAllowed" type="{http://docs.oasis-open.org/ns/cmis/core/200908/}enumContentStreamAllowed"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmisTypeDocumentDefinitionType", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/", propOrder = {
    "versionable",
    "contentStreamAllowed"
})
public class CmisTypeDocumentDefinitionType
    extends CmisTypeDefinitionType
{

    protected boolean versionable;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected EnumContentStreamAllowed contentStreamAllowed;

    /**
     * Gets the value of the versionable property.
     * 
     */
    public boolean isVersionable() {
        return versionable;
    }

    /**
     * Sets the value of the versionable property.
     * 
     */
    public void setVersionable(boolean value) {
        this.versionable = value;
    }

    /**
     * Gets the value of the contentStreamAllowed property.
     * 
     * @return
     *     possible object is
     *     {@link EnumContentStreamAllowed }
     *     
     */
    public EnumContentStreamAllowed getContentStreamAllowed() {
        return contentStreamAllowed;
    }

    /**
     * Sets the value of the contentStreamAllowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumContentStreamAllowed }
     *     
     */
    public void setContentStreamAllowed(EnumContentStreamAllowed value) {
        this.contentStreamAllowed = value;
    }

}
