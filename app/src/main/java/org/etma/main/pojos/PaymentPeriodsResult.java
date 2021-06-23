package org.etma.main.pojos;

public class PaymentPeriodsResult
{
    private PaymentPeriodItems[] items;

    private String totalCount;

    public PaymentPeriodItems[] getItems ()
    {
        return items;
    }

    public void setItems (PaymentPeriodItems[] items)
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