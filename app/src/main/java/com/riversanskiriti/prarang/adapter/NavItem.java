package com.riversanskiriti.prarang.adapter;

/**
 * Created by ARPIT on 07-03-2017.
 */

public class NavItem {
    private int icon;
    private String title;
    private Boolean switchValue = false;
    private int type = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public NavItem(String title, Boolean switchValue, int icon, int type) {
        this.title = title;
        this.switchValue = switchValue;
        this.icon = icon;
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int type) {
        this.icon = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getSwitchValue() {
        return switchValue;
    }

    public void setSwitchValue(Boolean switchValue) {
        this.switchValue = switchValue;
    }
}
