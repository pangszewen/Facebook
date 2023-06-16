package com.facebook.fullstackbackend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class GroupBuilder {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
    public String groupID;
    public String groupName;
    public String adminID;
    public ArrayList<String> members = new ArrayList<>();
    public int noOfMembers;
    public int noOfCreatedPost;
    public int noOfDeletedPost;
    public String dateOfCreation;

    public GroupBuilder(){
        this.groupID = null;
        this.groupName = null;
        this.adminID = null;
        this.members = new ArrayList<>();
        this.noOfMembers = 0;
        this.noOfCreatedPost = 0;
        this.noOfDeletedPost = 0;
        this.dateOfCreation = String.valueOf(LocalDate.now());
    }

    public GroupBuilder(User creator){
        this.groupID = database.generateID("groups", "groupID");
        this.groupName = null;
        this.adminID = creator.getAccountID();
        this.members.add(adminID);
        this.noOfMembers = 1;
        this.noOfCreatedPost = 0;
        this.noOfDeletedPost = 0;
        this.dateOfCreation = String.valueOf(LocalDate.now());
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

    public Group build(){
        return new Group(this);
    }

    public Group editGroupName(Group group){
        System.out.println("Current group name: " + group.getGroupName());
        System.out.print("Change group name to: ");
        System.out.println("*************************");
        String groupName = sc.nextLine();

        // Update group object
        group.setGroupName(groupName);

        // Update to database;
        database.updateGroup(group, "groupName", groupName);

        return group;
    }

    public Group changeAdmin(Group group){
        ArrayList<String> membersID = group.getMembers();
        ArrayList<String> membersUsername = new ArrayList<>();
        for(String x : membersID){
            User member = database.getProfile(x);
            if(!x.equals(group.getAdminID()))
                membersUsername.add(member.getUsername());
        }
        int choice = 1;
        while(choice==1){
            System.out.print("Enter the username of new admin:");
            String adminUsername = sc.nextLine();
            System.out.println("-------------------------");
            if(!membersUsername.contains(adminUsername)){
                choice = 2;
                while(choice>1 || choice<0){
                    System.out.println("No such member");
                    System.out.println("-------------------------");
                    System.out.println("0 - Back");
                    System.out.println("1 - Change new admin");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                }
            }else{
                choice = 3;
                while(choice>2 || choice<0){
                    User admin = database.getProfile(adminUsername);
                    System.out.println("New admin: " + admin.getName());
                    System.out.println("-------------------------");
                    System.out.println("0 - Back");
                    System.out.println("1 - Change new admin");
                    System.out.println("2 - Confirm");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                    if(choice==2){
                        // Update group object
                        group.setAdminID(admin.getAccountID());

                        // Update to database
                        database.updateGroup(group, "adminID", admin.getAccountID());
                    }
                }
            }
        }
        return group;
    }
}
