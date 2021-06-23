package org.etma.main.pojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInformationResult {

    @SerializedName("profilePictureId")
    @Expose
    private Object profilePictureId;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("roles")
    @Expose
    private List<Role> roles = null;
    @SerializedName("allOrganizationUnits")
    @Expose
    private List<AllOrganizationUnit> allOrganizationUnits = null;
    @SerializedName("memberedOrganizationUnits")
    @Expose
    private List<Object> memberedOrganizationUnits = null;

    public Object getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(Object profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<AllOrganizationUnit> getAllOrganizationUnits() {
        return allOrganizationUnits;
    }

    public void setAllOrganizationUnits(List<AllOrganizationUnit> allOrganizationUnits) {
        this.allOrganizationUnits = allOrganizationUnits;
    }

    public List<Object> getMemberedOrganizationUnits() {
        return memberedOrganizationUnits;
    }

    public void setMemberedOrganizationUnits(List<Object> memberedOrganizationUnits) {
        this.memberedOrganizationUnits = memberedOrganizationUnits;
    }

}

