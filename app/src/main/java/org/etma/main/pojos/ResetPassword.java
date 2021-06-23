package org.etma.main.pojos;

public class ResetPassword
{
    private String returnUrl;

    private String resetCode;

    private String c;

    private int userId;

    private String singleSignIn;

    private String password;

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSingleSignIn() {
        return singleSignIn;
    }

    public void setSingleSignIn(String singleSignIn) {
        this.singleSignIn = singleSignIn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
