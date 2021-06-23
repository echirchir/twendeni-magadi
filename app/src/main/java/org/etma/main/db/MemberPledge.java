package org.etma.main.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class MemberPledge extends RealmObject {

    @PrimaryKey
    @Index
    private long id;

    private long localPledgeId;
    private String lastModifierUserId;
    private String paymentPeriodId;
    private String deletionTime;
    private String datePledged;
    private String isDeleted;
    private String pledgeId;
    private String amount;
    private String balance;
    private String pledgeStakeId;
    private String creatorUserId;
    private String contributed;
    private String name;
    private String userId;
    private String deleterUserId;
    private String active;
    private String creationTime;
    private String memberId;
    private String initialPayment;
    private String lastModificationTime;
    private String startDate;
    private String endDate;

    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLocalPledgeId() {
        return localPledgeId;
    }

    public void setLocalPledgeId(long localPledgeId) {
        this.localPledgeId = localPledgeId;
    }

    public String getLastModifierUserId() {
        return lastModifierUserId;
    }

    public void setLastModifierUserId(String lastModifierUserId) {
        this.lastModifierUserId = lastModifierUserId;
    }

    public String getPaymentPeriodId() {
        return paymentPeriodId;
    }

    public void setPaymentPeriodId(String paymentPeriodId) {
        this.paymentPeriodId = paymentPeriodId;
    }

    public String getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(String deletionTime) {
        this.deletionTime = deletionTime;
    }

    public String getDatePledged() {
        return datePledged;
    }

    public void setDatePledged(String datePledged) {
        this.datePledged = datePledged;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getPledgeId() {
        return pledgeId;
    }

    public void setPledgeId(String pledgeId) {
        this.pledgeId = pledgeId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPledgeStakeId() {
        return pledgeStakeId;
    }

    public void setPledgeStakeId(String pledgeStakeId) {
        this.pledgeStakeId = pledgeStakeId;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getContributed() {
        return contributed;
    }

    public void setContributed(String contributed) {
        this.contributed = contributed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeleterUserId() {
        return deleterUserId;
    }

    public void setDeleterUserId(String deleterUserId) {
        this.deleterUserId = deleterUserId;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getInitialPayment() {
        return initialPayment;
    }

    public void setInitialPayment(String initialPayment) {
        this.initialPayment = initialPayment;
    }

    public String getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(String lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
