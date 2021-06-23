package org.etma.main.ui;

public class MpesaPaymentObject {

    private long id;

    private String date;

    private String amount;

    private String status;

    private String payMode;

    private String code;

    public MpesaPaymentObject(long id, String date, String amount, String status, String payMode, String code) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.status = status;
        this.payMode = payMode;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
