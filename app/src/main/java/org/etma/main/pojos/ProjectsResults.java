package org.etma.main.pojos;

public class ProjectsResults
{
    private ProjectResultItems[] items;

    private String totalCount;

    public ProjectResultItems[] getItems ()
    {
        return items;
    }

    public void setItems (ProjectResultItems[] items)
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
