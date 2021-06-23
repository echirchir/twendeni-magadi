package org.etma.main.events;

public class PasswordResetEmailSentEvent {

    private boolean success;

    public PasswordResetEmailSentEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
