package com.facebook.fullstackbackend.model;

import java.util.ArrayList;

public class Group {
    private String groupID;
    private String groupName;
    private String adminID;
    private ArrayList<String> members;
    private int noOfMembers;
    private int noOfCreatedPost;
    private int noOfDeletedPost;
    private String dateOfCreation;

    public Group(GroupBuilder builder){
        this.groupID = builder.groupID;
        this.groupName = builder.groupName;
        this.adminID = builder.adminID;
        this.members = builder.members;
        this.noOfMembers = builder.noOfMembers;
        this.noOfCreatedPost = builder.noOfCreatedPost;
        this.noOfDeletedPost = builder.noOfDeletedPost;
        this.dateOfCreation = builder.dateOfCreation;
    }

    public void setGroupID(String groupID){
        this.groupID = groupID;
    }
    public String getGroupID(){
        return groupID;
    }

    public void setGroupName(String groupName){
        this.groupName = groupName;
    }
    public String getGroupName(){
        return groupName;
    }

    public void setAdminID(String adminID){
        this.adminID = adminID;
    }
    public String getAdminID(){
        return adminID;
    }

    public void setMembers(ArrayList<String> members){
        this.members = members;
    }
    public void setMembers(String memberID){
        this.members.add(memberID);
    }
    public ArrayList<String> getMembers(){
        return members;
    }

    public void setNoOfMembers(int noOfMembers){
        this.noOfMembers = noOfMembers;
    }
    public int getNoOfMembers(){
        return noOfMembers;
    }

    public void setNoOfCreatedPost(int noOfCreatedPost){
        this.noOfCreatedPost = noOfCreatedPost;
    }
    public int getNoOfCreatedPost(){
        return noOfCreatedPost;
    }

    public void setNoOfDeletedPost(int noOfDeletedPost){
        this.noOfDeletedPost = noOfDeletedPost;
    }
    public int getNoOfDeletedPost(){
        return noOfDeletedPost;
    }

    public void setDateOfCreation(String dateOfCreation){
        this.dateOfCreation = dateOfCreation;
    }
    public String getDateOfCreation(){
        return dateOfCreation;
    }
}
