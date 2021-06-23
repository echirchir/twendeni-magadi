package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MemberPledgeResult {

    @SerializedName("totalCount")
    @Expose
    private int totalCount;
    @SerializedName("items")
    @Expose
    private List<MemberPledgeItem> items = null;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<MemberPledgeItem> getItems() {
        return items;
    }

    public void setItems(List<MemberPledgeItem> items) {
        this.items = items;
    }

}
