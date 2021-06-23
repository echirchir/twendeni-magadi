package org.etma.main.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class EtmaUser extends RealmObject {

    @PrimaryKey @Index
    private long id;
    private String full_name;
    private String surname;
    private String cell_phone;
    private String email_address;
    private String password;
    private String captcha_phrase;
    private boolean can_login;
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCell_phone() {
        return cell_phone;
    }

    public void setCell_phone(String cell_phone) {
        this.cell_phone = cell_phone;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha_phrase() {
        return captcha_phrase;
    }

    public void setCaptcha_phrase(String captcha_phrase) {
        this.captcha_phrase = captcha_phrase;
    }

    public boolean getCan_login() {
        return can_login;
    }

    public void setCan_login(boolean can_login) {
        this.can_login = can_login;
    }

    public boolean isCan_login() {
        return can_login;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
