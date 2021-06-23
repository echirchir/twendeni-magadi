package org.etma.main.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images {

    @SerializedName("full")
    @Expose
    private Full full;
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;
    @SerializedName("medium")
    @Expose
    private Medium medium;
    @SerializedName("medium_large")
    @Expose
    private MediumLarge mediumLarge;
    @SerializedName("post-thumbnail")
    @Expose
    private PostThumbnail postThumbnail;
    @SerializedName("holycross-thumb-350x350")
    @Expose
    private HolycrossThumb350x350 holycrossThumb350x350;
    @SerializedName("holycross-thumb-800x600")
    @Expose
    private HolycrossThumb800x600 holycrossThumb800x600;
    @SerializedName("holycross-thumb-800x500")
    @Expose
    private HolycrossThumb800x500 holycrossThumb800x500;
    @SerializedName("holycross-thumb-341x257")
    @Expose
    private HolycrossThumb341x257 holycrossThumb341x257;
    @SerializedName("holycross-thumb-800x450")
    @Expose
    private HolycrossThumb800x450 holycrossThumb800x450;
    @SerializedName("holycross-thumb-550x350")
    @Expose
    private HolycrossThumb550x350 holycrossThumb550x350;
    @SerializedName("holycross-thumb-360x148")
    @Expose
    private HolycrossThumb360x148 holycrossThumb360x148;

    public Full getFull() {
        return full;
    }

    public void setFull(Full full) {
        this.full = full;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public MediumLarge getMediumLarge() {
        return mediumLarge;
    }

    public void setMediumLarge(MediumLarge mediumLarge) {
        this.mediumLarge = mediumLarge;
    }

    public PostThumbnail getPostThumbnail() {
        return postThumbnail;
    }

    public void setPostThumbnail(PostThumbnail postThumbnail) {
        this.postThumbnail = postThumbnail;
    }

    public HolycrossThumb350x350 getHolycrossThumb350x350() {
        return holycrossThumb350x350;
    }

    public void setHolycrossThumb350x350(HolycrossThumb350x350 holycrossThumb350x350) {
        this.holycrossThumb350x350 = holycrossThumb350x350;
    }

    public HolycrossThumb800x600 getHolycrossThumb800x600() {
        return holycrossThumb800x600;
    }

    public void setHolycrossThumb800x600(HolycrossThumb800x600 holycrossThumb800x600) {
        this.holycrossThumb800x600 = holycrossThumb800x600;
    }

    public HolycrossThumb800x500 getHolycrossThumb800x500() {
        return holycrossThumb800x500;
    }

    public void setHolycrossThumb800x500(HolycrossThumb800x500 holycrossThumb800x500) {
        this.holycrossThumb800x500 = holycrossThumb800x500;
    }

    public HolycrossThumb341x257 getHolycrossThumb341x257() {
        return holycrossThumb341x257;
    }

    public void setHolycrossThumb341x257(HolycrossThumb341x257 holycrossThumb341x257) {
        this.holycrossThumb341x257 = holycrossThumb341x257;
    }

    public HolycrossThumb800x450 getHolycrossThumb800x450() {
        return holycrossThumb800x450;
    }

    public void setHolycrossThumb800x450(HolycrossThumb800x450 holycrossThumb800x450) {
        this.holycrossThumb800x450 = holycrossThumb800x450;
    }

    public HolycrossThumb550x350 getHolycrossThumb550x350() {
        return holycrossThumb550x350;
    }

    public void setHolycrossThumb550x350(HolycrossThumb550x350 holycrossThumb550x350) {
        this.holycrossThumb550x350 = holycrossThumb550x350;
    }

    public HolycrossThumb360x148 getHolycrossThumb360x148() {
        return holycrossThumb360x148;
    }

    public void setHolycrossThumb360x148(HolycrossThumb360x148 holycrossThumb360x148) {
        this.holycrossThumb360x148 = holycrossThumb360x148;
    }

}
