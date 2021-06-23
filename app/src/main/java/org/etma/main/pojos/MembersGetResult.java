package org.etma.main.pojos;

public class MembersGetResult
{
    private MembersGetItems[] items;

    private String totalCount;

    public MembersGetItems[] getItems ()
    {
        return items;
    }

    public void setItems (MembersGetItems[] items)
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

}
