package org.etma.main.ui;

public class MemberPledgeObject {

    private long id;
    private String memberName;
    private double amount;
    private String date;
    private long localPledgeId;
    private double paid;

    public MemberPledgeObject(long id, String memberName, double amount, String date, long localPledgeId, double paid) {
        this.id = id;
        this.memberName = memberName;
        this.amount = amount;
        this.date = date;
        this.localPledgeId = localPledgeId;
        this.paid = paid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getLocalPledgeId() {
        return localPledgeId;
    }

    public void setLocalPledgeId(long localPledgeId) {
        this.localPledgeId = localPledgeId;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }
}
