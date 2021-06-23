package org.etma.main.events;

public class SupportEmailSentEvent {

    private boolean success;

    public SupportEmailSentEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
