package org.etma.main.ui;

public class MemberObject {

    private long id;

    private String fullName;

    private String email;

    private String amount;

    public MemberObject(long id, String fullName, String email, String amount) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
