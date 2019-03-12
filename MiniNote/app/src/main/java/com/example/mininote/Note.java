package com.example.mininote;

import java.util.Date;
import java.util.UUID;

public class Note {//模型层
    private UUID id;
    private Date date;
    private String mTitle;//标题
    private Date notiDate;//联系人
    private String secondLine;//第二行的信息
    private String content;//文本内容
    private int setNoti ;//闹钟标志

    public Note(){
        this(UUID.randomUUID());
    }

    public Note(UUID id){
        this.id = id;
        date = new Date();
        notiDate = new Date();
    }

    public UUID getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getNotiDate() {
        return notiDate;
    }

    public void setNotiDate(Date notiDate) {
        this.notiDate = notiDate;
    }

    public String getPicFileName(){
        return "IMG_"+getId().toString()+".jpg";
    }

    public String getSecondLine() {
        return secondLine;
    }

    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSetNoti() {
        return setNoti;
    }

    public void setSetNoti(int setNoti) {
        this.setNoti = setNoti;
    }
}
