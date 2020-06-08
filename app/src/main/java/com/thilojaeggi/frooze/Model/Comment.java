package com.thilojaeggi.frooze.Model;

public class Comment {

    private String comment;
    private String publisher;
    private String key;
    private String postid;
    public Comment(String comment, String publisher, String key, String postid) {
        this.comment = comment;
        this.publisher = publisher;
        this.key = key;
        this.postid = postid;

    }
    public Comment(){
    }

    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public String getPublisher(){
        return publisher;
    }
    public void setPublisher(){
        this.publisher = publisher;
    }
    public String getKey(){
        return key;
    }
    public void setKey(){
        this.key = key;
    }
    public String getPostid(){
        return postid;
    }
    public void setPostid(){
        this.postid = postid;
    }

}
