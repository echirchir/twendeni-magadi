package org.etma.main.pojos;

public class MemberPledgeItems
{
    private MemberPledge memberPledge;

    private String pledgeStakeName;

    private String userName;

    private String memberFullName;

    private String paymentPeriodName;

    public MemberPledge getMemberPledge ()
    {
        return memberPledge;
    }

    public void setMemberPledge (MemberPledge memberPledge)
    {
        this.memberPledge = memberPledge;
    }

    public String getPledgeStakeName ()
    {
        return pledgeStakeName;
    }

    public void setPledgeStakeName (String pledgeStakeName)
    {
        this.pledgeStakeName = pledgeStakeName;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    public String getMemberFullName ()
    {
        return memberFullName;
    }

    public void setMemberFullName (String memberFullName)
    {
        this.memberFullName = memberFullName;
    }

    public String getPaymentPeriodName ()
    {
        return paymentPeriodName;
    }

    public void setPaymentPeriodName (String paymentPeriodName)
    {
        this.paymentPeriodName = paymentPeriodName;
    }

}