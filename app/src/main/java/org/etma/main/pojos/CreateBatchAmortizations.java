package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateBatchAmortizations {

    @SerializedName("list")
    @Expose
    private List<AmortizationList> list = null;

    public List<AmortizationList> getList() {
        return list;
    }

    public void setList(List<AmortizationList> list) {
        this.list = list;
    }

}
