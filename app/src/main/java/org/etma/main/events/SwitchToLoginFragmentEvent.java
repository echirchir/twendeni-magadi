package org.etma.main.events;

public class SwitchToLoginFragmentEvent {

    private boolean switchToLogin;

    public SwitchToLoginFragmentEvent(boolean switchToLogin) {
        this.switchToLogin = switchToLogin;
    }

    public boolean isSwitchToLogin() {
        return switchToLogin;
    }

    public void setSwitchToLogin(boolean switchToLogin) {
        this.switchToLogin = switchToLogin;
    }
}
