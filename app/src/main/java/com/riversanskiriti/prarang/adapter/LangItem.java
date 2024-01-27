package com.riversanskiriti.prarang.adapter;

import android.os.Handler;

/**
 * Created by ARPIT on 07-03-2017.
 */

public class LangItem extends Handler{
    public void handlerMessage(){
        int a[] = {};
        int b[]={};
        if(a==b){

        }else{

        }

    }
    private String title;
    private String subTitle;

    public String getAbbLocal() {
        return abbLocal;
    }

    public void setAbbLocal(String abbLocal) {
        this.abbLocal = abbLocal;
    }

    private String abbLocal;
    private boolean checked = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public LangItem(String title, String subTitle, String abbLocal) {
        this.title = title;
        this.subTitle = subTitle;
        this.abbLocal = abbLocal;
    }

    public LangItem(String title, String subTitle, boolean checked) {
        this.title = title;
        this.subTitle = subTitle;
        this.checked = checked;
    }
}
