package com.example.firebase.model;

public class Video {
    private String url;
    private String uploaderEmail;
    private long likeCount;
    private long dislikeCount;

    public Video() {}

    public Video(String url, String uploaderEmail) {
        this.url = url;
        this.uploaderEmail = uploaderEmail;
        this.likeCount = 0;
        this.dislikeCount = 0;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUploaderEmail() {
        return uploaderEmail;
    }
    public void setUploaderEmail(String uploaderEmail) {
        this.uploaderEmail = uploaderEmail;
    }
    public long getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
    public long getDislikeCount() {
        return dislikeCount;
    }
    public void setDislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
}
