package org.etma.main.pojos;

public class Authenticate
{
    private String returnUrl;

    private String singleSignIn;

    private String twoFactorVerificationCode;

    private String rememberClient;

    private String password;

    private String twoFactorRememberClientToken;

    private String userNameOrEmailAddress;

    public String getReturnUrl ()
    {
        return returnUrl;
    }

    public void setReturnUrl (String returnUrl)
    {
        this.returnUrl = returnUrl;
    }

    public String getSingleSignIn ()
    {
        return singleSignIn;
    }

    public void setSingleSignIn (String singleSignIn)
    {
        this.singleSignIn = singleSignIn;
    }

    public String getTwoFactorVerificationCode ()
    {
        return twoFactorVerificationCode;
    }

    public void setTwoFactorVerificationCode (String twoFactorVerificationCode)
    {
        this.twoFactorVerificationCode = twoFactorVerificationCode;
    }

    public String getRememberClient ()
    {
        return rememberClient;
    }

    public void setRememberClient (String rememberClient)
    {
        this.rememberClient = rememberClient;
    }

    public String getPassword ()
    {
        return password;
    }

    public void setPassword (String password)
    {
        this.password = password;
    }

    public String getTwoFactorRememberClientToken ()
    {
        return twoFactorRememberClientToken;
    }

    public void setTwoFactorRememberClientToken (String twoFactorRememberClientToken)
    {
        this.twoFactorRememberClientToken = twoFactorRememberClientToken;
    }

    public String getUserNameOrEmailAddress ()
    {
        return userNameOrEmailAddress;
    }

    public void setUserNameOrEmailAddress (String userNameOrEmailAddress)
    {
        this.userNameOrEmailAddress = userNameOrEmailAddress;
    }

}
