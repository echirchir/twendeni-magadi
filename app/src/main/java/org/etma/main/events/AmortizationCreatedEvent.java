package org.etma.main.events;

public class AmortizationCreatedEvent {

    private boolean success;

    public AmortizationCreatedEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
