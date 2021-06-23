package org.etma.main.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class PaymentPeriod extends RealmObject {

    @PrimaryKey @Index
    private long id;

    private String paymentPeriodId;
    private String mobileApp;
    private String description;
    private String webPortal;
    private String period;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPaymentPeriodId() {
        return paymentPeriodId;
    }

    public void setPaymentPeriodId(String paymentPeriodId) {
        this.paymentPeriodId = paymentPeriodId;
    }

    public String getMobileApp() {
        return mobileApp;
    }

    public void setMobileApp(String mobileApp) {
        this.mobileApp = mobileApp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebPortal() {
        return webPortal;
    }

    public void setWebPortal(String webPortal) {
        this.webPortal = webPortal;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
