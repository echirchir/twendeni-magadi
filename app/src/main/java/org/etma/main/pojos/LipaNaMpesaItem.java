package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LipaNaMpesaItem {

    @SerializedName("lipaNaMpesa")
    @Expose
    private LipaNaMpesa lipaNaMpesa;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("pledgeAmortizationMemberPledge")
    @Expose
    private String pledgeAmortizationMemberPledge;

    public LipaNaMpesa getLipaNaMpesa() {
        return lipaNaMpesa;
    }

    public void setLipaNaMpesa(LipaNaMpesa lipaNaMpesa) {
        this.lipaNaMpesa = lipaNaMpesa;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPledgeAmortizationMemberPledge() {
        return pledgeAmortizationMemberPledge;
    }

    public void setPledgeAmortizationMemberPledge(String pledgeAmortizationMemberPledge) {
        this.pledgeAmortizationMemberPledge = pledgeAmortizationMemberPledge;
    }

}
