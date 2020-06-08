package com.thilojaeggi.frooze.Model;

public class Hashtag {

    private String hashtag;

    public Hashtag(String hashtag)
    {
        this.hashtag = hashtag;
    }

    public Hashtag(){
    }

    public String getHashtag(){
        return hashtag;
    }
    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

}
