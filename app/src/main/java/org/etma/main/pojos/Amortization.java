package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Amortization {

    @SerializedName("contributionDate")
    @Expose
    private String contributionDate;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("dateContributed")
    @Expose
    private String dateContributed;
    @SerializedName("fullyContributed")
    @Expose
    private boolean fullyContributed;
    @SerializedName("contributed")
    @Expose
    private int contributed;
    @SerializedName("balance")
    @Expose
    private int balance;
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("memberPledgeId")
    @Expose
    private int memberPledgeId;
    @SerializedName("memberId")
    @Expose
    private int memberId;
    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("deleterUserId")
    @Expose
    private int deleterUserId;
    @SerializedName("deletionTime")
    @Expose
    private String deletionTime;
    @SerializedName("lastModificationTime")
    @Expose
    private String lastModificationTime;
    @SerializedName("lastModifierUserId")
    @Expose
    private int lastModifierUserId;
    @SerializedName("creationTime")
    @Expose
    private String creationTime;
    @SerializedName("creatorUserId")
    @Expose
    private int creatorUserId;
    @SerializedName("id")
    @Expose
    private String id;

    public String getContributionDate() {
        return contributionDate;
    }

    public void setContributionDate(String contributionDate) {
        this.contributionDate = contributionDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDateContributed() {
        return dateContributed;
    }

    public void setDateContributed(String dateContributed) {
        this.dateContributed = dateContributed;
    }

    public boolean isFullyContributed() {
        return fullyContributed;
    }

    public void setFullyContributed(boolean fullyContributed) {
        this.fullyContributed = fullyContributed;
    }

    public int getContributed() {
        return contributed;
    }

    public void setContributed(int contributed) {
        this.contributed = contributed;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMemberPledgeId() {
        return memberPledgeId;
    }

    public void setMemberPledgeId(int memberPledgeId) {
        this.memberPledgeId = memberPledgeId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getDeleterUserId() {
        return deleterUserId;
    }

    public void setDeleterUserId(int deleterUserId) {
        this.deleterUserId = deleterUserId;
    }

    public String getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(String deletionTime) {
        this.deletionTime = deletionTime;
    }

    public String getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(String lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public int getLastModifierUserId() {
        return lastModifierUserId;
    }

    public void setLastModifierUserId(int lastModifierUserId) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}


