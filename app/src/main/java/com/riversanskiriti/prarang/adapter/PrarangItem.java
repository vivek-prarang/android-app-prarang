package com.riversanskiriti.prarang.adapter;

import java.io.Serializable;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class PrarangItem implements Serializable {
    private String title, count, category;
    private int icon, type, frameColor;
    private int id;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PrarangItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
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

    public PrarangItem(String title, String count, int icon, int frameColor, int type) {
        this.title = title;
        this.count = count;
        this.icon = icon;
        this.frameColor = frameColor;
        this.type = type;
    }
}
