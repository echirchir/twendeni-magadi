package org.etma.main.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class PledgeStake extends RealmObject {

    @PrimaryKey @Index
    private long id;

    private String pledgeStakeId;
    private String mobileApp;
    private String stake;
    private String minimum;
    private String description;
    private String maximum;
    private String webApp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPledgeStakeId() {
        return pledgeStakeId;
    }

    public void setPledgeStakeId(String pledgeStakeId) {
        this.pledgeStakeId = pledgeStakeId;
    }

    public String getMobileApp() {
        return mobileApp;
    }

    public void setMobileApp(String mobileApp) {
        this.mobileApp = mobileApp;
    }

    public String getStake() {
        return stake;
    }

    public void setStake(String stake) {
        this.stake = stake;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getWebApp() {
        return webApp;
    }

    public void setWebApp(String webApp) {
        this.webApp = webApp;
    }
}
