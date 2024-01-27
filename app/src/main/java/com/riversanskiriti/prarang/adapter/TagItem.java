package com.riversanskiriti.prarang.adapter;

import java.io.Serializable;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class TagItem implements Serializable {
    private String title, id, iconUrl;
    private int icon, frameColor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconUrl() {
        if (!iconUrl.startsWith("http")) {
            iconUrl = "http://" + iconUrl;
        }
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public TagItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFrameColor() {
        return frameColor;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public TagItem(String title, int icon, int frameColor) {
        this.title = title;
        this.icon = icon;
        this.frameColor = frameColor;
    }
}
