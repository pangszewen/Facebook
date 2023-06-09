package com.facebook.fullstackbackend.model;

public class PostBuilder {
    // Total 7 attributes
    public String postID;
    public String userID;
    public Post.Status status;
    public String content;
    public int likes;
    public int comments;
    public String postTime;

    
    public PostBuilder(){
        this.postID = null;
        this.userID = null;
        this.status = Post.Status.PUBLIC;
        this.likes = 0;
        this.comments = 0;
    }

    public PostBuilder(User user){
        this.postID = user.getAccountID() + "U" + String.valueOf(user.getNoOfCreatedPost());
        this.userID = user.getAccountID();
        this.status = Post.Status.PUBLIC;
        this.likes = 0;
        this.comments = 0;
    }

    public PostBuilder(Group group, User user){
        this.postID = group.getGroupID() + "G" +String.valueOf(group.getNoOfCreatedPost());
        this.userID = user.getAccountID();
        this.status = Post.Status.GROUP;
        this.likes = 0;
        this.comments = 0;
    }

    // Getter and setter method 
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

    // Setter method used to create new post.
    public void setStatus(Post.Status status){
        this.status = status;
    }
    // Setter method used to create Post object.
    public void setStatus(String status){
        this.status = Post.Status.valueOf(status);
    }
    public Post.Status getStatus(){
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

    public Post build(){
        return new Post(this);
    }
}
