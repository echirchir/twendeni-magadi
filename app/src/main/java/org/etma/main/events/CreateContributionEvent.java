package org.etma.main.events;

public class CreateContributionEvent {

    private boolean success;

    public CreateContributionEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
