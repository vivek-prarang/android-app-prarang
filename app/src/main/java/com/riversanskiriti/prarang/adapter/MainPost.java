package com.riversanskiriti.prarang.adapter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ARPIT on 09-03-2017.
 */

public class MainPost implements Serializable {
    private List<PostItem> list;
    private String name;
    private String message = "";
    private boolean loading = true;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MainPost() {

    }

    public MainPost(String name, List<PostItem> list) {
        this.list = list;
        this.name = name;
    }

    public List<PostItem> getList() {
        return list;
    }

    public void setList(List<PostItem> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
