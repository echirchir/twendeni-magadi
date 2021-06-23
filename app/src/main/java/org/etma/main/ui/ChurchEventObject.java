package org.etma.main.ui;

public class ChurchEventObject {

    private long id;
    private String title;
    private String description;
    private String img_url;
    private String date;
    private String author;

    public ChurchEventObject(long id, String title, String description, String img_url, String date, String author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.img_url = img_url;
        this.date = date;
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
