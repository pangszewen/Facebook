package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class GroupBuilder {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
    // Total 8 attributes
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
    }

    public GroupBuilder(User creator){
        // Group ID is auto-generated.
        this.groupID = database.generateID("groups", "groupID");
        this.groupName = null;
        this.adminID = creator.getAccountID();
        this.members.add(adminID);
        this.noOfMembers = 1;
        this.noOfCreatedPost = 0;
        this.noOfDeletedPost = 0;
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

    // Method to edit group name by admin.
    public Group editGroupName(Group group){
        try{
            // Display current group name and prompt user to enter new group name.
            System.out.println("Current group name: " + group.getGroupName());
            System.out.print("Change group name to: ");
            String groupName = sc.nextLine();
            System.out.println("*************************");

            int choice = 2;
            while(choice!=0 && choice!=1){
                System.out.println("0 - Back");
                System.out.println("1 - Confirm changes");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }
            if(choice==1){
                // Update group object
                group.setGroupName(groupName);

                // Update to database;
                database.updateGroup(group, "groupName", groupName);
            }

            return group;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editGroupName(group);
        }
        System.out.println("Failed to edit group name");
        return group;
    }

    // Method to change group admin to another group member.
    public Group changeAdmin(Group group){
        try{
            ArrayList<String> membersID = group.getMembers();
            ArrayList<String> membersUsername = new ArrayList<>();
            for(String x : membersID){
                User member = database.getProfile(x);
                if(!x.equals(group.getAdminID()))   // Admin is not included into the searching list.
                    membersUsername.add(member.getUsername());
            }
            int choice = 1;
            while(choice==1){
                System.out.print("Enter the username of new admin:");
                String newAdminUsername = sc.nextLine();
                System.out.println("-------------------------");
                if(!membersUsername.contains(newAdminUsername)){
                    choice = 2;
                    while(choice!=0 && choice!=2){
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
                        User newAdmin = database.getProfile(newAdminUsername);
                        System.out.println("New admin: " + newAdmin.getName());
                        System.out.println("-------------------------");
                        System.out.println("0 - Back");
                        System.out.println("1 - Change new admin");
                        System.out.println("2 - Confirm changes");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                        if(choice==2){
                            // Update group object
                            group.setAdminID(newAdmin.getAccountID());

                            // Update to database
                            database.updateGroup(group, "adminID", newAdmin.getAccountID());
                        }
                    }
                }
            }
            return group;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            changeAdmin(group);
        }
        System.out.println("Failed to change group admin");
        return group;
    }
}
