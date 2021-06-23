package org.etma.main.pojos;

public class PaymentModesResult
{
    private PaymentModeItems[] items;

    private String totalCount;

    public PaymentModeItems[] getItems ()
    {
        return items;
    }

    public void setItems (PaymentModeItems[] items)
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