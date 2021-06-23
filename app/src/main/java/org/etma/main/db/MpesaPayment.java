package org.etma.main.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class MpesaPayment extends RealmObject{

    @PrimaryKey @Index
    private long id;

    private int amortizationId;
    private String respMerchantRequestID;
    private String respCode;
    private String lastModifierUserId;
    private String deletionTime;
    private String status;
    private String respDesc;
    private String resultDesc;
    private String isDeleted;
    private String creatorUserId;
    private String amountToPay;
    private String respCheckoutRequestID;
    private String mpesaReceiptNumber;
    private String phoneNumber;
    private String userId;
    private String deleterUserId;
    private String resultCode;
    private String respCustMsg;
    private String creationTime;
    private String requestDate;
    private String lastModificationTime;
    private String accountRefDesc;
    private int paymentModeId;
    private int mpesaPaymentId;
    private String custom1;
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAmortizationId() {
        return amortizationId;
    }

    public void setAmortizationId(int amortizationId) {
        this.amortizationId = amortizationId;
    }

    public String getRespMerchantRequestID() {
        return respMerchantRequestID;
    }

    public void setRespMerchantRequestID(String respMerchantRequestID) {
        this.respMerchantRequestID = respMerchantRequestID;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getLastModifierUserId() {
        return lastModifierUserId;
    }

    public void setLastModifierUserId(String lastModifierUserId) {
        this.lastModifierUserId = lastModifierUserId;
    }

    public String getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(String deletionTime) {
        this.deletionTime = deletionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(String creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(String amountToPay) {
        this.amountToPay = amountToPay;
    }

    public String getRespCheckoutRequestID() {
        return respCheckoutRequestID;
    }

    public void setRespCheckoutRequestID(String respCheckoutRequestID) {
        this.respCheckoutRequestID = respCheckoutRequestID;
    }

    public String getMpesaReceiptNumber() {
        return mpesaReceiptNumber;
    }

    public void setMpesaReceiptNumber(String mpesaReceiptNumber) {
        this.mpesaReceiptNumber = mpesaReceiptNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getRespCustMsg() {
        return respCustMsg;
    }

    public void setRespCustMsg(String respCustMsg) {
        this.respCustMsg = respCustMsg;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getLastModificationTime() {
        return lastModificationTime;
    }

    public void setLastModificationTime(String lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
    }

    public String getAccountRefDesc() {
        return accountRefDesc;
    }

    public void setAccountRefDesc(String accountRefDesc) {
        this.accountRefDesc = accountRefDesc;
    }

    public int getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(int paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public int getMpesaPaymentId() {
        return mpesaPaymentId;
    }

    public void setMpesaPaymentId(int mpesaPaymentId) {
        this.mpesaPaymentId = mpesaPaymentId;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
