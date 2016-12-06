
package org.apache.chemistry.opencmis.commons.impl.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enumAllowableActionsKey.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="enumAllowableActionsKey"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="canGetDescendents.Folder"/&gt;
 *     &lt;enumeration value="canGetChildren.Folder"/&gt;
 *     &lt;enumeration value="canGetParents.Folder"/&gt;
 *     &lt;enumeration value="canGetFolderParent.Object"/&gt;
 *     &lt;enumeration value="canCreateDocument.Folder"/&gt;
 *     &lt;enumeration value="canCreateFolder.Folder"/&gt;
 *     &lt;enumeration value="canCreateRelationship.Source"/&gt;
 *     &lt;enumeration value="canCreateRelationship.Target"/&gt;
 *     &lt;enumeration value="canGetProperties.Object"/&gt;
 *     &lt;enumeration value="canViewContent.Object"/&gt;
 *     &lt;enumeration value="canUpdateProperties.Object"/&gt;
 *     &lt;enumeration value="canMove.Object"/&gt;
 *     &lt;enumeration value="canMove.Target"/&gt;
 *     &lt;enumeration value="canMove.Source"/&gt;
 *     &lt;enumeration value="canDelete.Object"/&gt;
 *     &lt;enumeration value="canDeleteTree.Folder"/&gt;
 *     &lt;enumeration value="canSetContent.Document"/&gt;
 *     &lt;enumeration value="canDeleteContent.Document"/&gt;
 *     &lt;enumeration value="canAddToFolder.Object"/&gt;
 *     &lt;enumeration value="canAddToFolder.Folder"/&gt;
 *     &lt;enumeration value="canRemoveFromFolder.Object"/&gt;
 *     &lt;enumeration value="canRemoveFromFolder.Folder"/&gt;
 *     &lt;enumeration value="canCheckout.Document"/&gt;
 *     &lt;enumeration value="canCancelCheckout.Document"/&gt;
 *     &lt;enumeration value="canCheckin.Document"/&gt;
 *     &lt;enumeration value="canGetAllVersions.VersionSeries"/&gt;
 *     &lt;enumeration value="canGetObjectRelationships.Object"/&gt;
 *     &lt;enumeration value="canAddPolicy.Object"/&gt;
 *     &lt;enumeration value="canAddPolicy.Policy"/&gt;
 *     &lt;enumeration value="canRemovePolicy.Object"/&gt;
 *     &lt;enumeration value="canRemovePolicy.Policy"/&gt;
 *     &lt;enumeration value="canGetAppliedPolicies.Object"/&gt;
 *     &lt;enumeration value="canGetACL.Object"/&gt;
 *     &lt;enumeration value="canApplyACL.Object"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "enumAllowableActionsKey", namespace = "http://docs.oasis-open.org/ns/cmis/core/200908/")
@XmlEnum
public enum EnumAllowableActionsKey {

    @XmlEnumValue("canGetDescendents.Folder")
    CAN_GET_DESCENDENTS_FOLDER("canGetDescendents.Folder"),
    @XmlEnumValue("canGetChildren.Folder")
    CAN_GET_CHILDREN_FOLDER("canGetChildren.Folder"),
    @XmlEnumValue("canGetParents.Folder")
    CAN_GET_PARENTS_FOLDER("canGetParents.Folder"),
    @XmlEnumValue("canGetFolderParent.Object")
    CAN_GET_FOLDER_PARENT_OBJECT("canGetFolderParent.Object"),
    @XmlEnumValue("canCreateDocument.Folder")
    CAN_CREATE_DOCUMENT_FOLDER("canCreateDocument.Folder"),
    @XmlEnumValue("canCreateFolder.Folder")
    CAN_CREATE_FOLDER_FOLDER("canCreateFolder.Folder"),
    @XmlEnumValue("canCreateRelationship.Source")
    CAN_CREATE_RELATIONSHIP_SOURCE("canCreateRelationship.Source"),
    @XmlEnumValue("canCreateRelationship.Target")
    CAN_CREATE_RELATIONSHIP_TARGET("canCreateRelationship.Target"),
    @XmlEnumValue("canGetProperties.Object")
    CAN_GET_PROPERTIES_OBJECT("canGetProperties.Object"),
    @XmlEnumValue("canViewContent.Object")
    CAN_VIEW_CONTENT_OBJECT("canViewContent.Object"),
    @XmlEnumValue("canUpdateProperties.Object")
    CAN_UPDATE_PROPERTIES_OBJECT("canUpdateProperties.Object"),
    @XmlEnumValue("canMove.Object")
    CAN_MOVE_OBJECT("canMove.Object"),
    @XmlEnumValue("canMove.Target")
    CAN_MOVE_TARGET("canMove.Target"),
    @XmlEnumValue("canMove.Source")
    CAN_MOVE_SOURCE("canMove.Source"),
    @XmlEnumValue("canDelete.Object")
    CAN_DELETE_OBJECT("canDelete.Object"),
    @XmlEnumValue("canDeleteTree.Folder")
    CAN_DELETE_TREE_FOLDER("canDeleteTree.Folder"),
    @XmlEnumValue("canSetContent.Document")
    CAN_SET_CONTENT_DOCUMENT("canSetContent.Document"),
    @XmlEnumValue("canDeleteContent.Document")
    CAN_DELETE_CONTENT_DOCUMENT("canDeleteContent.Document"),
    @XmlEnumValue("canAddToFolder.Object")
    CAN_ADD_TO_FOLDER_OBJECT("canAddToFolder.Object"),
    @XmlEnumValue("canAddToFolder.Folder")
    CAN_ADD_TO_FOLDER_FOLDER("canAddToFolder.Folder"),
    @XmlEnumValue("canRemoveFromFolder.Object")
    CAN_REMOVE_FROM_FOLDER_OBJECT("canRemoveFromFolder.Object"),
    @XmlEnumValue("canRemoveFromFolder.Folder")
    CAN_REMOVE_FROM_FOLDER_FOLDER("canRemoveFromFolder.Folder"),
    @XmlEnumValue("canCheckout.Document")
    CAN_CHECKOUT_DOCUMENT("canCheckout.Document"),
    @XmlEnumValue("canCancelCheckout.Document")
    CAN_CANCEL_CHECKOUT_DOCUMENT("canCancelCheckout.Document"),
    @XmlEnumValue("canCheckin.Document")
    CAN_CHECKIN_DOCUMENT("canCheckin.Document"),
    @XmlEnumValue("canGetAllVersions.VersionSeries")
    CAN_GET_ALL_VERSIONS_VERSION_SERIES("canGetAllVersions.VersionSeries"),
    @XmlEnumValue("canGetObjectRelationships.Object")
    CAN_GET_OBJECT_RELATIONSHIPS_OBJECT("canGetObjectRelationships.Object"),
    @XmlEnumValue("canAddPolicy.Object")
    CAN_ADD_POLICY_OBJECT("canAddPolicy.Object"),
    @XmlEnumValue("canAddPolicy.Policy")
    CAN_ADD_POLICY_POLICY("canAddPolicy.Policy"),
    @XmlEnumValue("canRemovePolicy.Object")
    CAN_REMOVE_POLICY_OBJECT("canRemovePolicy.Object"),
    @XmlEnumValue("canRemovePolicy.Policy")
    CAN_REMOVE_POLICY_POLICY("canRemovePolicy.Policy"),
    @XmlEnumValue("canGetAppliedPolicies.Object")
    CAN_GET_APPLIED_POLICIES_OBJECT("canGetAppliedPolicies.Object"),
    @XmlEnumValue("canGetACL.Object")
    CAN_GET_ACL_OBJECT("canGetACL.Object"),
    @XmlEnumValue("canApplyACL.Object")
    CAN_APPLY_ACL_OBJECT("canApplyACL.Object");
    private final String value;

    EnumAllowableActionsKey(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnumAllowableActionsKey fromValue(String v) {
        for (EnumAllowableActionsKey c: EnumAllowableActionsKey.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
