package org.etma.main.events;

public class LipaNaMpesaExtraMileEvent {

    private boolean success;

    private String payLoad;

    private String status;

    public LipaNaMpesaExtraMileEvent(boolean success, String payLoad, String status) {
        this.success = success;
        this.payLoad = payLoad;
        this.status = status;
    }

    public LipaNaMpesaExtraMileEvent(boolean success) {
        this.success = success;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
