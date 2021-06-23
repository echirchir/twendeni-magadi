package org.etma.main.pojos;

public class Result
{
    private Items[] items;

    private String totalCount;

    public Items[] getItems ()
    {
        return items;
    }

    public void setItems (Items[] items)
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
