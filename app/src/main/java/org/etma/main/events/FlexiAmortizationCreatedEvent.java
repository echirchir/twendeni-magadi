package org.etma.main.events;

public class FlexiAmortizationCreatedEvent {

    private boolean success;

    public FlexiAmortizationCreatedEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
