package org.etma.main.pojos;

public class AmortizationResult
{
    private AmortizationResultItems[] items;

    private String totalCount;

    public AmortizationResultItems[] getItems ()
    {
        return items;
    }

    public void setItems (AmortizationResultItems[] items)
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
