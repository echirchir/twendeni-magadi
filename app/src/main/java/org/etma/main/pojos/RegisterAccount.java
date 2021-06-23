package org.etma.main.pojos;

public class RegisterAccount {

    private String captchaResponse;
    private String name;
    private String emailAddress;
    private String userName;
    private String surname;
    private String password;

    public String getCaptchaResponse ()
    {
        return captchaResponse;
    }

    public void setCaptchaResponse (String captchaResponse) {
        this.captchaResponse = captchaResponse;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getEmailAddress ()
    {
        return emailAddress;
    }

    public void setEmailAddress (String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    public String getSurname ()
    {
        return surname;
    }

    public void setSurname (String surname)
    {
        this.surname = surname;
    }

    public String getPassword ()
    {
        return password;
    }

    public void setPassword (String password)
    {
        this.password = password;
    }

}