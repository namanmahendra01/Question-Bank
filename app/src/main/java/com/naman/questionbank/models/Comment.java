package com.naman.questionbank.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment{
    private String c;
    private String ui;
    private String tim;

    public Comment(String c, String ui, String tim) {
        this.c = c;
        this.ui = ui;
        this.tim = tim;
    }

    public Comment() {

    }



    public String getC() {
        return c;
    }

    public void setC(String comment) {
        this.c = comment;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String user_id) {
        this.ui = user_id;
    }

    public String getTim() {
        return tim;
    }

    public void setTim(String tim) {
        this.tim = tim;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "c='" + c + '\'' +
                ", ui='" + ui + '\'' +
                ", dc='" + tim + '\'' +
                '}';
    }
}

