package com.naman.questionbank.models;

public class Question {

    String ui, tim,tg,que,v, qi;

    public Question(String ui, String tim, String tg, String que, String v,String qi) {
        this.ui = ui;
        this.tim = tim;
        this.tg = tg;
        this.que = que;
        this.v = v;
        this.qi = qi;
    }
    public Question(){}

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

    public String getQue() {
        return que;
    }

    public void setQue(String que) {
        this.que = que;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;

    }

    public String getQi() {
        return qi;
    }

    public void setQi(String qi) {
        this.qi = qi;

    }

    @Override
    public String toString() {
        return "Question{" +
                "ui='" + ui + '\'' +
                ", tim='" + tim + '\'' +
                ", tg='" + tg + '\'' +
                ", que='" + que + '\'' +
                ", v='" + v + '\'' +
                ", qi='" + qi + '\'' +

                '}';
    }
}
