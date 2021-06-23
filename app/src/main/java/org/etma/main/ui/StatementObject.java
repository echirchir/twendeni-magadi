package org.etma.main.ui;

public class StatementObject {

    private long id;
    private String type;
    private String refNumber;
    private String date;
    private String description;
    private String amount;

    public StatementObject(long id, String type, String refNumber, String date, String description, String amount) {
        this.id = id;
        this.type = type;
        this.refNumber = refNumber;
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
