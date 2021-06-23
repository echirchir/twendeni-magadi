package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllOrganizationUnit {

    @SerializedName("parentId")
    @Expose
    private int parentId;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("memberCount")
    @Expose
    private int memberCount;
    @SerializedName("lastModificationTime")
    @Expose
    private Object lastModificationTime;
    @SerializedName("lastModifierUserId")
    @Expose
    private Object lastModifierUserId;
    @SerializedName("creationTime")
    @Expose
    private String creationTime;
    @SerializedName("creatorUserId")
    @Expose
    private int creatorUserId;
    @SerializedName("id")
    @Expose
    private int id;

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public Object getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(Object lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public Object getLastModifierUserId() {
        return lastModifierUserId;
    }

    public void setLastModifierUserId(Object lastModifierUserId) {
        this.lastModifierUserId = lastModifierUserId;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public int getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(int creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
