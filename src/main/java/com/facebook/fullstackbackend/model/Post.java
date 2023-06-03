package com.facebook.fullstackbackend.model;

public class Post {
    private String postID;
    private String userID;
    private Status status;
    private String content;
    private int likes;
    private int comments;
    private String postTime;
    enum Status{
        PUBLIC,
        PRIVATE
    }

    public Post(PostBuilder builder){
        this.postID = builder.postID;
        this.userID = builder.userID;
        this.status = builder.status;
        this.content = builder.content;
        this.likes = builder.likes;
        this.comments = builder.comments;
        this.postTime = builder.postTime;
    }

    public void setPostID(String postID){
        this.postID = postID;
    }
    public String getPostID(){
        return postID;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }
    public String getUserID(){
        return userID;
    }

    public void setStatus(Status status){
        this.status = status;
    }
    public Status geStatus(){
        return status;
    }

    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return content;
    }

    public void setLikes(int likes){
        this.likes = likes;
    }
    public int getLikes(){
        return likes;
    }

    public void setComments(int comments){
        this.comments = comments;
    }
    public int getComments(){
        return comments;
    }

    public void setPostTime(String postTime){
        this.postTime = postTime;
    }
    public String getPostTime(){
        return this.postTime;
    }
}
