package org.etma.main.events;

public class LoginSuccessEvent {

    private boolean success;

    public LoginSuccessEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
