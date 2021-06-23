package org.etma.main.pojos;

public class AmortizationResultItems
{
    private PledgeAmortization pledgeAmortization;

    private String userName;

    private String memberFullName;

    private String memberPledgeName;

    public PledgeAmortization getPledgeAmortization ()
    {
        return pledgeAmortization;
    }

    public void setPledgeAmortization (PledgeAmortization pledgeAmortization)
    {
        this.pledgeAmortization = pledgeAmortization;
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

    public String getMemberPledgeName ()
    {
        return memberPledgeName;
    }

    public void setMemberPledgeName (String memberPledgeName)
    {
        this.memberPledgeName = memberPledgeName;
    }

}
