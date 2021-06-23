package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PledgeContributionItem {

    @SerializedName("pledgeContribution")
    @Expose
    private PledgeContribution pledgeContribution;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("memberPledgeName")
    @Expose
    private String memberPledgeName;
    @SerializedName("memberFullName")
    @Expose
    private String memberFullName;
    @SerializedName("paymentModeName")
    @Expose
    private String paymentModeName;
    @SerializedName("pledgeAmortizationAmount")
    @Expose
    private String pledgeAmortizationAmount;

    public PledgeContribution getPledgeContribution() {
        return pledgeContribution;
    }

    public void setPledgeContribution(PledgeContribution pledgeContribution) {
        this.pledgeContribution = pledgeContribution;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMemberPledgeName() {
        return memberPledgeName;
    }

    public void setMemberPledgeName(String memberPledgeName) {
        this.memberPledgeName = memberPledgeName;
    }

    public String getMemberFullName() {
        return memberFullName;
    }

    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }

    public String getPaymentModeName() {
        return paymentModeName;
    }

    public void setPaymentModeName(String paymentModeName) {
        this.paymentModeName = paymentModeName;
    }

    public String getPledgeAmortizationAmount() {
        return pledgeAmortizationAmount;
    }

    public void setPledgeAmortizationAmount(String pledgeAmortizationAmount) {
        this.pledgeAmortizationAmount = pledgeAmortizationAmount;
    }

}