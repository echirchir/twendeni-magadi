package org.etma.main.events;

public class FlexiPledgeCreatedEvent {

    private boolean success;

    private int pledgeId;

    public FlexiPledgeCreatedEvent(boolean success, int pledgeId) {
        this.success = success;
        this.pledgeId = pledgeId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getPledgeId() {
        return pledgeId;
    }

    public void setPledgeId(int pledgeId) {
        this.pledgeId = pledgeId;
    }
}
