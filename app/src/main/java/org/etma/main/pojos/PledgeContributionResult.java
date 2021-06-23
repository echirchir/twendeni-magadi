package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PledgeContributionResult {

    @SerializedName("totalCount")
    @Expose
    private int totalCount;
    @SerializedName("items")
    @Expose
    private List<PledgeContributionItem> items = null;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<PledgeContributionItem> getItems() {
        return items;
    }

    public void setItems(List<PledgeContributionItem> items) {
        this.items = items;
    }

}
