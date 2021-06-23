package org.etma.main.pojos;

public class PledgeAmortization
{
    private String id;
    private String amount;
    private String balance;
    private String contributed;
    private String fullyContributed;
    private String dateContributed;
    private String userId;
    private String contributionDate;
    private String memberId;
    private String memberPledgeId;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getAmount ()
    {
        return amount;
    }

    public void setAmount (String amount)
    {
        this.amount = amount;
    }

    public String getBalance ()
    {
        return balance;
    }

    public void setBalance (String balance)
    {
        this.balance = balance;
    }

    public String getContributed ()
    {
        return contributed;
    }

    public void setContributed (String contributed)
    {
        this.contributed = contributed;
    }

    public String getFullyContributed ()
    {
        return fullyContributed;
    }

    public void setFullyContributed (String fullyContributed)
    {
        this.fullyContributed = fullyContributed;
    }

    public String getDateContributed ()
    {
        return dateContributed;
    }

    public void setDateContributed (String dateContributed)
    {
        this.dateContributed = dateContributed;
    }

    public String getUserId ()
    {
        return userId;
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public String getContributionDate ()
    {
        return contributionDate;
    }

    public void setContributionDate (String contributionDate)
    {
        this.contributionDate = contributionDate;
    }

    public String getMemberId ()
    {
        return memberId;
    }

    public void setMemberId (String memberId)
    {
        this.memberId = memberId;
    }

    public String getMemberPledgeId ()
    {
        return memberPledgeId;
    }

    public void setMemberPledgeId (String memberPledgeId)
    {
        this.memberPledgeId = memberPledgeId;
    }

}
