package com.thilojaeggi.frooze.Model;

import android.app.Activity;

public class Post {
    private String postid;
    private String postvideo;
    private String description;
    private String publisher;
    private String dangerous;
    public Post(String postid, String postvideo, String description, String publisher, String dangerous) {
        this.postid = postid;
        this.dangerous = dangerous;
        this.postvideo = postvideo;
        this.description = description;
        this.publisher = publisher;
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
