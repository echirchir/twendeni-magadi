package org.etma.main.adapters;

public class DashboardItem {

    private long id;
    private int icon;
    private int iconArrow;
    private String title;
    private String amount;

    public DashboardItem(long id, int icon, int iconArrow, String title, String amount) {
        this.id = id;
        this.icon = icon;
        this.iconArrow = iconArrow;
        this.title = title;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIconArrow() {
        return iconArrow;
    }

    public void setIconArrow(int iconArrow) {
        this.iconArrow = iconArrow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
