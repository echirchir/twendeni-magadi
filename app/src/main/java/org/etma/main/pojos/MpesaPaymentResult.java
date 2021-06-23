package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MpesaPaymentResult {

    @SerializedName("totalCount")
    @Expose
    private int totalCount;
    @SerializedName("items")
    @Expose
    private List<LipaNaMpesaItem> items = null;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<LipaNaMpesaItem> getItems() {
        return items;
    }

    public void setItems(List<LipaNaMpesaItem> items) {
        this.items = items;
    }

}
