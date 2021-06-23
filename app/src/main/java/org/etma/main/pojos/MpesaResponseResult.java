package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MpesaResponseResult {

    @SerializedName("respCheckoutRequestID")
    @Expose
    private String respCheckoutRequestID;
    @SerializedName("respCode")
    @Expose
    private String respCode;
    @SerializedName("respCustMsg")
    @Expose
    private String respCustMsg;
    @SerializedName("respMerchantRequestID")
    @Expose
    private String respMerchantRequestID;
    @SerializedName("respDesc")
    @Expose
    private String respDesc;

    public String getRespCheckoutRequestID() {
        return respCheckoutRequestID;
    }

    public void setRespCheckoutRequestID(String respCheckoutRequestID) {
        this.respCheckoutRequestID = respCheckoutRequestID;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespCustMsg() {
        return respCustMsg;
    }

    public void setRespCustMsg(String respCustMsg) {
        this.respCustMsg = respCustMsg;
    }

    public String getRespMerchantRequestID() {
        return respMerchantRequestID;
    }

    public void setRespMerchantRequestID(String respMerchantRequestID) {
        this.respMerchantRequestID = respMerchantRequestID;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

}


