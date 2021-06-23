package org.etma.main.pojos;

public class Project
{
    private String id;
    private String startDate;
    private String lastPledgeAmount;
    private String totalContributions;
    private String lastContrAmount;
    private String totalPledges;
    private String projectTarget;
    private String endDate;
    private String projectName;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getStartDate ()
    {
        return startDate;
    }

    public void setStartDate (String startDate)
    {
        this.startDate = startDate;
    }

    public String getLastPledgeAmount ()
    {
        return lastPledgeAmount;
    }

    public void setLastPledgeAmount (String lastPledgeAmount)
    {
        this.lastPledgeAmount = lastPledgeAmount;
    }

    public String getTotalContributions ()
    {
        return totalContributions;
    }

    public void setTotalContributions (String totalContributions)
    {
        this.totalContributions = totalContributions;
    }

    public String getLastContrAmount ()
    {
        return lastContrAmount;
    }

    public void setLastContrAmount (String lastContrAmount)
    {
        this.lastContrAmount = lastContrAmount;
    }

    public String getTotalPledges ()
    {
        return totalPledges;
    }

    public void setTotalPledges (String totalPledges)
    {
        this.totalPledges = totalPledges;
    }

    public String getProjectTarget ()
    {
        return projectTarget;
    }

    public void setProjectTarget (String projectTarget)
    {
        this.projectTarget = projectTarget;
    }

    public String getEndDate ()
    {
        return endDate;
    }

    public void setEndDate (String endDate)
    {
        this.endDate = endDate;
    }

    public String getProjectName ()
    {
        return projectName;
    }

    public void setProjectName (String projectName)
    {
        this.projectName = projectName;
    }

}
