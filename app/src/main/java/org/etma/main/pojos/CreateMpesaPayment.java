package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateMpesaPayment {

    @SerializedName("amountToPay")
    @Expose
    private int amountToPay;
    @SerializedName("requestDate")
    @Expose
    private String requestDate;
    @SerializedName("accountRefDesc")
    @Expose
    private String accountRefDesc;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("status")
    @Expose
    private String status;
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
    @SerializedName("mpesaReceiptNumber")
    @Expose
    private String mpesaReceiptNumber;
    @SerializedName("resultCode")
    @Expose
    private String resultCode;
    @SerializedName("resultDesc")
    @Expose
    private String resultDesc;
    @SerializedName("respDesc")
    @Expose
    private String respDesc;
    @SerializedName("userId")
    @Expose
    private int userId;
    @SerializedName("pledgeAmortizationId")
    @Expose
    private int pledgeAmortizationId;
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

    private String custom1;


    public int getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(int amountToPay) {
        this.amountToPay = amountToPay;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getAccountRefDesc() {
        return accountRefDesc;
    }

    public void setAccountRefDesc(String accountRefDesc) {
        this.accountRefDesc = accountRefDesc;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getMpesaReceiptNumber() {
        return mpesaReceiptNumber;
    }

    public void setMpesaReceiptNumber(String mpesaReceiptNumber) {
        this.mpesaReceiptNumber = mpesaReceiptNumber;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPledgeAmortizationId() {
        return pledgeAmortizationId;
    }

    public void setPledgeAmortizationId(int pledgeAmortizationId) {
        this.pledgeAmortizationId = pledgeAmortizationId;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }
}

