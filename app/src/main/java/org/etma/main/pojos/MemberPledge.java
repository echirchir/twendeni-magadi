package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberPledge {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("datePledged")
    @Expose
    private String datePledged;
    @SerializedName("initialPayment")
    @Expose
    private int initialPayment;
    @SerializedName("contributed")
    @Expose
    private int contributed;
    @SerializedName("balance")
    @Expose
    private int balance;
    @SerializedName("active")
    @Expose
    private boolean active;
    @SerializedName("memberId")
    @Expose
    private int memberId;
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("pledgeStakeId")
    @Expose
    private int pledgeStakeId;
    @SerializedName("paymentPeriodId")
    @Expose
    private int paymentPeriodId;
    @SerializedName("id")
    @Expose
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDatePledged() {
        return datePledged;
    }

    public void setDatePledged(String datePledged) {
        this.datePledged = datePledged;
    }

    public int getInitialPayment() {
        return initialPayment;
    }

    public void setInitialPayment(int initialPayment) {
        this.initialPayment = initialPayment;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPledgeStakeId() {
        return pledgeStakeId;
    }

    public void setPledgeStakeId(int pledgeStakeId) {
        this.pledgeStakeId = pledgeStakeId;
    }

    public int getPaymentPeriodId() {
        return paymentPeriodId;
    }

    public void setPaymentPeriodId(int paymentPeriodId) {
        this.paymentPeriodId = paymentPeriodId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}