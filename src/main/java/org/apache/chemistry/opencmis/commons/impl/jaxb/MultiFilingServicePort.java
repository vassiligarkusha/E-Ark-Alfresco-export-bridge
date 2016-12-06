package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.0.9
 * 2016-08-15T08:42:24.718+03:00
 * Generated source version: 3.0.9
 * 
 */
@WebService(targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", name = "MultiFilingServicePort")
@XmlSeeAlso({ObjectFactory.class})
public interface MultiFilingServicePort {

    @WebMethod(action = "addObjectToFolder")
    @RequestWrapper(localName = "addObjectToFolder", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/", className = "org.apache.chemistry.opencmis.commons.impl.jaxb.AddObjectToFolder")
    @ResponseWrapper(localName = "addObjectToFolderResponse", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/", className = "org.apache.chemistry.opencmis.commons.impl.jaxb.AddObjectToFolderResponse")
    public void addObjectToFolder(
        @WebParam(name = "repositoryId", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        java.lang.String repositoryId,
        @WebParam(name = "objectId", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        java.lang.String objectId,
        @WebParam(name = "folderId", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        java.lang.String folderId,
        @WebParam(name = "allVersions", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        java.lang.Boolean allVersions,
        @WebParam(mode = WebParam.Mode.INOUT, name = "extension", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        javax.xml.ws.Holder<CmisExtensionType> extension
    ) throws CmisException;

    @WebMethod(action = "removeObjectFromFolder")
    @RequestWrapper(localName = "removeObjectFromFolder", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/", className = "org.apache.chemistry.opencmis.commons.impl.jaxb.RemoveObjectFromFolder")
    @ResponseWrapper(localName = "removeObjectFromFolderResponse", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/", className = "org.apache.chemistry.opencmis.commons.impl.jaxb.RemoveObjectFromFolderResponse")
    public void removeObjectFromFolder(
        @WebParam(name = "repositoryId", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        java.lang.String repositoryId,
        @WebParam(name = "objectId", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        java.lang.String objectId,
        @WebParam(name = "folderId", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        java.lang.String folderId,
        @WebParam(mode = WebParam.Mode.INOUT, name = "extension", targetNamespace = "http://docs.oasis-open.org/ns/cmis/messaging/200908/")
        javax.xml.ws.Holder<CmisExtensionType> extension
    ) throws CmisException;
}
