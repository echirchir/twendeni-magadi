package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberPledgeItem {

    @SerializedName("memberPledge")
    @Expose
    private MemberPledge memberPledge;
    @SerializedName("memberFullName")
    @Expose
    private String memberFullName;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("pledgeStakeName")
    @Expose
    private String pledgeStakeName;
    @SerializedName("paymentPeriodName")
    @Expose
    private String paymentPeriodName;

    public MemberPledge getMemberPledge() {
        return memberPledge;
    }

    public void setMemberPledge(MemberPledge memberPledge) {
        this.memberPledge = memberPledge;
    }

    public String getMemberFullName() {
        return memberFullName;
    }

    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPledgeStakeName() {
        return pledgeStakeName;
    }

    public void setPledgeStakeName(String pledgeStakeName) {
        this.pledgeStakeName = pledgeStakeName;
    }

    public String getPaymentPeriodName() {
        return paymentPeriodName;
    }

    public void setPaymentPeriodName(String paymentPeriodName) {
        this.paymentPeriodName = paymentPeriodName;
    }

}