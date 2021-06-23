package org.etma.main.events;

public class AmortizationReceivedEvent {

    private boolean success;
    private int total;

    public AmortizationReceivedEvent(boolean success) {
        this.success = success;
    }

    public AmortizationReceivedEvent(boolean success, int total) {
        this.success = success;
        this.total =  total;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
