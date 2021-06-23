package org.etma.main.events;

public class UpdateUserProfileEvent {

    private boolean success;

    public UpdateUserProfileEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
