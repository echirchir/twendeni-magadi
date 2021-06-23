package org.etma.main.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class FlexiAmortization extends RealmObject {

    @Index @PrimaryKey
    private long id;

    private String contributionDate;
    private int amount;
    private String dateContributed;
    private boolean fullyContributed;
    private int contributed;
    private int balance;
    private int userId;
    private int memberPledgeId;
    private int memberId;
    private boolean isDeleted;
    private int deleterUserId;
    private String deletionTime;
    private String lastModificationTime;
    private int lastModifierUserId;
    private String creationTime;
    private int creatorUserId;
    private int amortizationId;
    private long localPledgeId;
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public int getAmortizationId() {
        return amortizationId;
    }

    public void setAmortizationId(int amortizationId) {
        this.amortizationId = amortizationId;
    }

    public long getLocalPledgeId() {
        return localPledgeId;
    }

    public void setLocalPledgeId(long localPledgeId) {
        this.localPledgeId = localPledgeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
