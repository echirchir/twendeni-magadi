package org.etma.main.pojos;

public class PledgeStakesResult
{
    private PledgeStakeItems[] items;

    private String totalCount;

    public PledgeStakeItems[] getItems ()
    {
        return items;
    }

    public void setItems (PledgeStakeItems[] items)
    {
        this.items = items;
    }

    public String getTotalCount ()
    {
        return totalCount;
    }

    public void setTotalCount (String totalCount)
    {
        this.totalCount = totalCount;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [items = "+items+", totalCount = "+totalCount+"]";
    }
}
