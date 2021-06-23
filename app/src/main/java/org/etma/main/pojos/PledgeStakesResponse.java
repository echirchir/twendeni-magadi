package org.etma.main.pojos;

public class PledgeStakesResponse {

    private String unAuthorizedRequest;

    private PledgeStakesResult result;

    private String error;

    private String __abp;

    private String success;

    private String targetUrl;

    public String getUnAuthorizedRequest ()
    {
        return unAuthorizedRequest;
    }

    public void setUnAuthorizedRequest (String unAuthorizedRequest)
    {
        this.unAuthorizedRequest = unAuthorizedRequest;
    }

    public PledgeStakesResult getResult ()
    {
        return result;
    }

    public void setResult (PledgeStakesResult result)
    {
        this.result = result;
    }

    public String getError ()
    {
        return error;
    }

    public void setError (String error)
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

    public String getTargetUrl ()
    {
        return targetUrl;
    }

    public void setTargetUrl (String targetUrl)
    {
        this.targetUrl = targetUrl;
    }

}