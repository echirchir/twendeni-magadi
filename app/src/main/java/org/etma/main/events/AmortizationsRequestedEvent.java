package org.etma.main.events;

public class AmortizationsRequestedEvent {

    private boolean isDone;

    public AmortizationsRequestedEvent(boolean isDone) {
        this.isDone = isDone;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
