package org.etma.main.ui;

public class ContributionObject {

    private long id;

    private  String amount;

    private String paidBy;

    private String verified;

    private String pledgeName;

    private String contributionDate;

    private String payMode;

    public ContributionObject(long id, String amount, String paidBy, String verified, String pledgeName, String contributionDate, String payMode) {
        this.id = id;
        this.amount = amount;
        this.paidBy = paidBy;
        this.verified = verified;
        this.pledgeName = pledgeName;
        this.contributionDate = contributionDate;
        this.payMode = payMode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getContributionDate() {
        return contributionDate;
    }

    public void setContributionDate(String contributionDate) {
        this.contributionDate = contributionDate;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getPledgeName() {
        return pledgeName;
    }

    public void setPledgeName(String pledgeName) {
        this.pledgeName = pledgeName;
    }
}
