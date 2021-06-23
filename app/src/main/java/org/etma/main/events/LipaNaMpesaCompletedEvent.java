package org.etma.main.events;

public class LipaNaMpesaCompletedEvent {

    private boolean success;

    private String payLoad;

    private String message;

    public LipaNaMpesaCompletedEvent(boolean success, String payLoad, String message) {
        this.success = success;
        this.payLoad = payLoad;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
