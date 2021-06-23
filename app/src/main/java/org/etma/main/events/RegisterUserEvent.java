package org.etma.main.events;

public class RegisterUserEvent {

    private boolean canLogin;

    private String message;

    public RegisterUserEvent(boolean canLogin, String message) {
        this.canLogin = canLogin;
        this.message = message;
    }

    public boolean isCanLogin() {
        return canLogin;
    }

    public void setCanLogin(boolean canLogin) {
        this.canLogin = canLogin;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
