package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomFields {

    @SerializedName("slz_options")
    @Expose
    private List<String> slzOptions = null;
    @SerializedName("slz_option:status")
    @Expose
    private List<String> slzOptionStatus = null;
    @SerializedName("slz_option:event_ticket_price")
    @Expose
    private List<String> slzOptionEventTicketPrice = null;
    @SerializedName("slz_option:event_ticket_number")
    @Expose
    private List<String> slzOptionEventTicketNumber = null;
    @SerializedName("slz_option:event_date_range")
    @Expose
    private List<String> slzOptionEventDateRange = null;
    @SerializedName("slz_option:event_location")
    @Expose
    private List<String> slzOptionEventLocation = null;
    @SerializedName("dfiFeatured")
    @Expose
    private List<String> dfiFeatured = null;
    @SerializedName("slide_template")
    @Expose
    private List<String> slideTemplate = null;
    @SerializedName("slz_option:from_date")
    @Expose
    private List<String> slzOptionFromDate = null;
    @SerializedName("slz_option:to_date")
    @Expose
    private List<String> slzOptionToDate = null;

    public List<String> getSlzOptions() {
        return slzOptions;
    }

    public void setSlzOptions(List<String> slzOptions) {
        this.slzOptions = slzOptions;
    }

    public List<String> getSlzOptionStatus() {
        return slzOptionStatus;
    }

    public void setSlzOptionStatus(List<String> slzOptionStatus) {
        this.slzOptionStatus = slzOptionStatus;
    }

    public List<String> getSlzOptionEventTicketPrice() {
        return slzOptionEventTicketPrice;
    }

    public void setSlzOptionEventTicketPrice(List<String> slzOptionEventTicketPrice) {
        this.slzOptionEventTicketPrice = slzOptionEventTicketPrice;
    }

    public List<String> getSlzOptionEventTicketNumber() {
        return slzOptionEventTicketNumber;
    }

    public void setSlzOptionEventTicketNumber(List<String> slzOptionEventTicketNumber) {
        this.slzOptionEventTicketNumber = slzOptionEventTicketNumber;
    }

    public List<String> getSlzOptionEventDateRange() {
        return slzOptionEventDateRange;
    }

    public void setSlzOptionEventDateRange(List<String> slzOptionEventDateRange) {
        this.slzOptionEventDateRange = slzOptionEventDateRange;
    }

    public List<String> getSlzOptionEventLocation() {
        return slzOptionEventLocation;
    }

    public void setSlzOptionEventLocation(List<String> slzOptionEventLocation) {
        this.slzOptionEventLocation = slzOptionEventLocation;
    }

    public List<String> getDfiFeatured() {
        return dfiFeatured;
    }

    public void setDfiFeatured(List<String> dfiFeatured) {
        this.dfiFeatured = dfiFeatured;
    }

    public List<String> getSlideTemplate() {
        return slideTemplate;
    }

    public void setSlideTemplate(List<String> slideTemplate) {
        this.slideTemplate = slideTemplate;
    }

    public List<String> getSlzOptionFromDate() {
        return slzOptionFromDate;
    }

    public void setSlzOptionFromDate(List<String> slzOptionFromDate) {
        this.slzOptionFromDate = slzOptionFromDate;
    }

    public List<String> getSlzOptionToDate() {
        return slzOptionToDate;
    }

    public void setSlzOptionToDate(List<String> slzOptionToDate) {
        this.slzOptionToDate = slzOptionToDate;
    }

}
