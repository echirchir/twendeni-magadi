package org.etma.main.events;

public class PasswordChangedEvent {

    private boolean success;

    public PasswordChangedEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
