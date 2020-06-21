package com.thilojaeggi.frooze.Model;

import android.app.Activity;

public class Post {
    private String postid;
    private String postvideo;
    private String description;
    private String publisher;
    private String dangerous;
    private String textcolor;
    public Post(String postid, String postvideo, String description, String publisher, String dangerous, String textcolor) {
        this.postid = postid;
        this.dangerous = dangerous;
        this.postvideo = postvideo;
        this.description = description;
        this.publisher = publisher;
        this.textcolor = textcolor;
    }

    public Post() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostvideo() {
        return postvideo;
    }

    public String getTextColor() {
        return textcolor;
    }
    public void setTextColor(String textcolor) {
        this.textcolor = textcolor;
    }

    public void setPostvideo(String postvideo) {
        this.postvideo = postvideo;
    }

    public String getDescription() {
        return description;
    }

    public String getDangerous(){
        return dangerous;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

}
