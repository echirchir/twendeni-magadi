package org.etma.main.pojos;

public class PledgeAmortizationResponse
{
    private String unAuthorizedRequest;

    private AmortizationResult result;

    private Error error;

    private String __abp;

    private String success;

    private Object targetUrl;

    public String getUnAuthorizedRequest ()
    {
        return unAuthorizedRequest;
    }

    public void setUnAuthorizedRequest (String unAuthorizedRequest)
    {
        this.unAuthorizedRequest = unAuthorizedRequest;
    }

    public AmortizationResult getResult ()
    {
        return result;
    }

    public void setResult (AmortizationResult result)
    {
        this.result = result;
    }

    public Error getError ()
    {
        return error;
    }

    public void setError (Error error)
    {
        this.error = error;
    }

    public String get__abp ()
    {
        return __abp;
    }

    public void set__abp (String __abp)
    {
        this.__abp = __abp;
    }

    public String getSuccess ()
    {
        return success;
    }

    public void setSuccess (String success)
    {
        this.success = success;
    }

    public Object getTargetUrl ()
    {
        return targetUrl;
    }

    public void setTargetUrl (Object targetUrl)
    {
        this.targetUrl = targetUrl;
    }

}
