package com.naman.questionbank.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Resource {

    String ui, tim,tg,ttl,lk,ri;

    public Resource(String ui, String tim, String tg, String ttl, String lk,String ri) {
        this.ui = ui;
        this.tim = tim;
        this.tg = tg;
        this.ttl = ttl;
        this.lk = lk;
        this.ri = ri;
    }
    public Resource(){}

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getTim() {
        return tim;
    }

    public void setTim(String tim) {
        this.tim = tim;
    }

    public String getTg() {
        return tg;
    }

    public void setTg(String tg) {
        this.tg = tg;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getLk() {
        return lk;
    }

    public void setLk(String lk) {
        this.lk = lk;

    }

    public String getRi() {
        return ri;
    }

    public void setRi(String ri) {
        this.ri = ri;

    }

    @Override
    public String toString() {
        return "Resource{" +
                "ui='" + ui + '\'' +
                ", tim='" + tim + '\'' +
                ", tg='" + tg + '\'' +
                ", ttl='" + ttl + '\'' +
                ", lk='" + lk + '\'' +
                ", ri='" + ri + '\'' +

                '}';
    }
}
