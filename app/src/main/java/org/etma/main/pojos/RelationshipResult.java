package org.etma.main.pojos;

public class RelationshipResult {

    private MemberRelationshipItems[] items;

    private String totalCount;

    public MemberRelationshipItems[] getItems ()
    {
        return items;
    }

    public void setItems (MemberRelationshipItems[] items)
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
