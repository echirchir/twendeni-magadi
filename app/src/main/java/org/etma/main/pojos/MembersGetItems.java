package org.etma.main.pojos;

public class MembersGetItems
{
    private Member member;

    private String userName;

    private String memberRelationshipName;

    public Member getMember ()
    {
        return member;
    }

    public void setMember (Member member)
    {
        this.member = member;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    public String getMemberRelationshipName ()
    {
        return memberRelationshipName;
    }

    public void setMemberRelationshipName (String memberRelationshipName)
    {
        this.memberRelationshipName = memberRelationshipName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [member = "+member+", userName = "+userName+", memberRelationshipName = "+memberRelationshipName+"]";
    }
}
