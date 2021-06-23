package org.etma.main.pojos;

public class Items {

    private Member member;

    private String memberRelationshipRelationship;

    private String userName;

    public Member getMember ()
    {
        return member;
    }

    public void setMember (Member member)
    {
        this.member = member;
    }

    public String getMemberRelationshipRelationship ()
    {
        return memberRelationshipRelationship;
    }

    public void setMemberRelationshipRelationship (String memberRelationshipRelationship)
    {
        this.memberRelationshipRelationship = memberRelationshipRelationship;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [member = "+member+", memberRelationshipRelationship = "+memberRelationshipRelationship+", userName = "+userName+"]";
    }
}
