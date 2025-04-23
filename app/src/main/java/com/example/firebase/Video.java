package com.example.firebase;

public class Video {
    public String url;
    public String posterUid;
    public long  likes, dislikes;
    public Video() {}
    public Video(String url, String posterUid) {
        this.url = url; this.posterUid = posterUid;
        this.likes = 0;   this.dislikes = 0;
    }
}
