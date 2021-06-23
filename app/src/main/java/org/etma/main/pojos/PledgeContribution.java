package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PledgeContribution {

    @SerializedName("accountNumber")
    @Expose
    private String accountNumber;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("contributionDate")
    @Expose
    private String contributionDate;
    @SerializedName("paidBy")
    @Expose
    private String paidBy;
    @SerializedName("refNumber")
    @Expose
    private String refNumber;
    @SerializedName("custom1")
    @Expose
    private String custom1;
    @SerializedName("custom2")
    @Expose
    private String custom2;
    @SerializedName("custom3")
    @Expose
    private String custom3;
    @SerializedName("custom4")
    @Expose
    private String custom4;
    @SerializedName("verified")
    @Expose
    private boolean verified;
    @SerializedName("verifiedBy")
    @Expose
    private String verifiedBy;
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("memberPledgeId")
    @Expose
    private int memberPledgeId;
    @SerializedName("memberId")
    @Expose
    private int memberId;
    @SerializedName("paymentModeId")
    @Expose
    private int paymentModeId;
    @SerializedName("pledgeAmortizationId")
    @Expose
    private int pledgeAmortizationId;
    @SerializedName("id")
    @Expose
    private int id;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getContributionDate() {
        return contributionDate;
    }

    public void setContributionDate(String contributionDate) {
        this.contributionDate = contributionDate;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
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

    public int getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(int paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public int getPledgeAmortizationId() {
        return pledgeAmortizationId;
    }

    public void setPledgeAmortizationId(int pledgeAmortizationId) {
        this.pledgeAmortizationId = pledgeAmortizationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
