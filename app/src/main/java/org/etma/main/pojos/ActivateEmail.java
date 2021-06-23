package org.etma.main.pojos;

public class ActivateEmail
{
    private String c;

    private String userId;

    private String confirmationCode;

    public String getC ()
    {
        return c;
    }

    public void setC (String c)
    {
        this.c = c;
    }

    public String getUserId ()
    {
        return userId;
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public String getConfirmationCode ()
    {
        return confirmationCode;
    }

    public void setConfirmationCode (String confirmationCode)
    {
        this.confirmationCode = confirmationCode;
    }

}
