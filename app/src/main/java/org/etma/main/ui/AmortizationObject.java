package org.etma.main.ui;

public class AmortizationObject {

    private long id;

    private long localPledgeId;

    private String contributionDate;

    private int amount;

    private int contributed;

    private int balance;

    private int amortizationId;

    public AmortizationObject(long id, long localPledgeId, String contributionDate, int amount, int contributed, int balance, int amortizationId) {
        this.id = id;
        this.localPledgeId = localPledgeId;
        this.contributionDate = contributionDate;
        this.amount = amount;
        this.contributed = contributed;
        this.balance = balance;
        this.amortizationId = amortizationId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLocalPledgeId() {
        return localPledgeId;
    }

    public void setLocalPledgeId(long localPledgeId) {
        this.localPledgeId = localPledgeId;
    }

    public String getContributionDate() {
        return contributionDate;
    }

    public void setContributionDate(String contributionDate) {
        this.contributionDate = contributionDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getContributed() {
        return contributed;
    }

    public void setContributed(int contributed) {
        this.contributed = contributed;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getAmortizationId() {
        return amortizationId;
    }

    public void setAmortizationId(int amortizationId) {
        this.amortizationId = amortizationId;
    }
}
