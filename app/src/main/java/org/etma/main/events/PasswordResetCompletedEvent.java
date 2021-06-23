package org.etma.main.events;

public class PasswordResetCompletedEvent {

    private boolean success;

    public PasswordResetCompletedEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
