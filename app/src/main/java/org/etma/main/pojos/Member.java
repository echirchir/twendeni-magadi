package org.etma.main.pojos;

public class Member {

    private String id;
    private String custom1;
    private String custom2;
    private String custom3;
    private String memberRelationshipId;
    private String cellphone;
    private String userId;
    private String emailAddress;
    private String fullName;
    private String active;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getCustom1 ()
    {
        return custom1;
    }

    public void setCustom1 (String custom1)
    {
        this.custom1 = custom1;
    }

    public String getCustom2 ()
    {
        return custom2;
    }

    public void setCustom2 (String custom2)
    {
        this.custom2 = custom2;
    }

    public String getCustom3 ()
    {
        return custom3;
    }

    public void setCustom3 (String custom3)
    {
        this.custom3 = custom3;
    }

    public String getMemberRelationshipId ()
    {
        return memberRelationshipId;
    }

    public void setMemberRelationshipId (String memberRelationshipId)
    {
        this.memberRelationshipId = memberRelationshipId;
    }

    public String getCellphone ()
    {
        return cellphone;
    }

    public void setCellphone (String cellphone)
    {
        this.cellphone = cellphone;
    }

    public String getUserId ()
    {
        return userId;
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public String getEmailAddress ()
    {
        return emailAddress;
    }

    public void setEmailAddress (String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getFullName ()
    {
        return fullName;
    }

    public void setFullName (String fullName)
    {
        this.fullName = fullName;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
