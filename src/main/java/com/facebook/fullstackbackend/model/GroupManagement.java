package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class GroupManagement {
    Scanner sc = new Scanner(System.in);
    GroupBuilder groupBuilder = new GroupBuilder();
    DatabaseSql<String> database = new DatabaseSql<>();
    DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
    PostManagement postManager = new PostManagement();
    Chat chat = new Chat();

    public User createGroup(User creator){
        try{
            GroupBuilder builder = new GroupBuilder(creator);
            System.out.println("Create Group");
            System.out.println("-------------------------");
            System.out.print("Group name: ");
            builder.setGroupName(sc.nextLine());
            System.out.println("*************************");
            Group group = builder.build();
            int choice = 2;
            while(choice!=1){
                System.out.println("Invite group members:");
                System.out.println("1 - Continue");
                System.out.println("2 - Invite member");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                switch(choice){
                    case 2: inviteMember(group, creator);
                }
            }
            choice = 2;
            while(choice!=0 && choice!=1){
                System.out.println("0 - Quit create group");
                System.out.println("1 - Create group");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }
            if(choice==1){
                database.createGroup(group); 
                chat.createGroupChat(group);
                // Update user groups
                creator.setGroups(group.getGroupID());
                database.updateUserProfile(creator, "groups", String.join(",", creator.getGroups()));
            }

            return creator;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            createGroup(creator);
        }
        System.out.println("Failed to create group");
        return creator;
    }

    public void viewGroupInfo(Group group){
        User admin = database.getProfile(group.getAdminID());
        System.out.println("About Group");
        System.out.println("-------------------------");
        System.out.println("Admin: " + admin.getName());
        System.out.println("No of members: " + group.getNoOfMembers());
        System.out.println("Created on: " + group.getDateOfCreation());
        System.out.println("*************************");
    }

    public Group editGroupInfo(Group group){
        try{
            String adminID = group.getAdminID();
            int choice = 1;
            while(choice>0 && adminID.equals(group.getAdminID())){
                System.out.println("Edit Group Info");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                System.out.println("1 - Group name");
                System.out.println("2 - Change admin");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                switch(choice){
                    case 1 -> group = groupBuilder.editGroupName(group);
                    case 2 -> group = groupBuilder.changeAdmin(group);
                }
            }
            return group;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editGroupInfo(group);
        }
        System.out.println("Failed to edit group info");
        return group;
    }

    public void inviteMember(Group group, User inviter){
        try{
            int choice = 1;
            while(choice>0){
                choice = 1;     // Initialization
                System.out.println("Enter search keyword:");
                String emailOrPhoneNoOrUsernameOrName =  sc.nextLine();
                System.out.println("*************************");
                ArrayList<User> result = database.ifContains(emailOrPhoneNoOrUsernameOrName);     // ArrayList of user objects of search result
                ArrayList<String> usernameResult = new ArrayList<>();

                for(User x: result){
                    usernameResult.add(x.getUsername());
                }
                result.clear();
                for(String x : usernameResult){
                    User u1 = database.getProfile(x);
                    if(!isMember(group, u1))
                        result.add(u1);
                }

                // Sort the names alphabetically
                for(int i=1; i<result.size(); i++){
                    for(int j=0; j<i; j++){
                        if(result.get(i).getName().compareTo(result.get(j).getName())<0){
                            User temp = result.get(i);
                            result.set(i, result.get(j));
                            result.set(j, temp);
                        }
                    }
                }

                if(result.size()==0){
                    choice = 2;
                    while(choice>1){
                        System.out.println("No result found.");
                        System.out.println("-------------------------");
                        System.out.println("0 - Back");
                        System.out.println("1 - Search again");
                        System.out.println("-1 - Back to Friends tab");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                    }
                    if(choice == 1)
                        choice = result.size()+1;
                }

                while(choice>0 && choice<result.size()+1){
                    // Display all search result
                    System.out.println("0 - Back");
                    int count = 1;
                    for(User x : result){
                        System.out.println(count + " - " + x.getName());
                        count++;
                    }
            
                    System.out.println("-------------------------");
                    System.out.println(result.size()+1 + " - Search again");
                    System.out.println("*************************");
                    // Select to view searched account
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");

                    // Condition if selection out of index bound
                    while(choice>result.size()+1){
                        System.out.println("Choice out of bound. Please select again.");
                        choice = sc.nextInt();
                    }

                    // If choice in range, view account; else continue
                    if(choice>0 && choice<result.size()+1){
                        int choiceInvite = 1;
                        while(choiceInvite>0){
                            User u1 = result.get(choice-1);
                            System.out.println(u1.getName());
                            System.out.println("-------------------------");
                            System.out.println("0 - Back");
                            if(checkInvitation(group, inviter, u1))
                                System.out.println("1 - Uninvite");
                            else
                                System.out.println("1 - Invite");
                            System.out.println("*************************");
                            choiceInvite = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                            if(choiceInvite==1){
                                if(!checkInvitation(group, inviter, u1)){
                                    u1.setGroupInvitations(inviter, group);
                                    database.updateUserProfile(u1, "groupInvitations", String.join(",", u1.getGroupInvitations()));
                                }else{
                                    u1 = cancelInvitation(group, inviter, u1);
                                }
                            }
                        }
                    }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            inviteMember(group, inviter);
        }
    }

    public Group removeMember(Group group){
        try{
            int choice = 1;
            while(choice>0){
                choice = 1;     // Initialization
                System.out.println("Enter search keyword:");
                String emailOrPhoneNoOrUsernameOrName =  sc.nextLine();
                System.out.println("*************************");
                ArrayList<User> result = database.ifContains(emailOrPhoneNoOrUsernameOrName);     // ArrayList of user objects of search result
                ArrayList<String> usernameResult = new ArrayList<>();

                for(User x: result){
                    usernameResult.add(x.getUsername());
                }
                result.clear();
                for(String x : usernameResult){
                    User u1 = database.getProfile(x);
                    if(isMember(group, u1))
                        result.add(u1);
                }

                // Sort the names alphabetically
                for(int i=1; i<result.size(); i++){
                    for(int j=0; j<i; j++){
                        if(result.get(i).getName().compareTo(result.get(j).getName())<0){
                            User temp = result.get(i);
                            result.set(i, result.get(j));
                            result.set(j, temp);
                        }
                    }
                }

                if(result.size()==0){
                    choice = 2;
                    while(choice>1){
                        System.out.println("No result found.");
                        System.out.println("-------------------------");
                        System.out.println("0 - Back");
                        System.out.println("1 - Search again");
                        System.out.println("-1 - Back to Friends tab");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                    }
                    if(choice == 1)
                        choice = result.size()+1;
                }

                while(choice>0 && choice<result.size()+1){
                    // Display all search result
                    System.out.println("0 - Back");
                    int count = 1;
                    for(User x : result){
                        System.out.println(count + " - " + x.getName());
                        count++;
                    }
            
                    System.out.println("-------------------------");
                    System.out.println(result.size()+1 + " - Search again");
                    System.out.println("*************************");
                    // Select to view searched account
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");

                    // Condition if selection out of index bound
                    while(choice>result.size()+1){
                        System.out.println("Choice out of bound. Please select again.");
                        choice = sc.nextInt();
                    }

                    // If choice in range, view account; else continue
                    while(choice>0 && choice<result.size()+1){
                        User user = result.get(choice-1);
                        System.out.println(user.getName());
                        System.out.println("-------------------------");
                        System.out.println("0 - Back");
                        System.out.println("1 - Remove member");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                        if(choice==1){
                            if(!isGroupAdmin(group, user)){
                                group = leaveGroup(group, user);
                                choice = 0;     // Break loop
                            }else{
                                System.out.println("You are an admin. You are not allowed to be removed from the group.");
                                System.out.println("*************************");
                            }
                        }else if(choice>1){
                            choice = 1;     // Maintain in loop
                        }
                    }
                }
            }
            return group;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            removeMember(group);
        }
        System.out.println("Failed to remove member");
        return group;
    }

    public Group joinGroup(Group group, User user){
        // Update group members
        ArrayList<String> members = group.getMembers();
        members.add(user.getAccountID());
        group.setMembers(members);
        group.setNoOfMembers(group.getNoOfMembers()+1);
        database.updateGroup(group, "members", String.join(",", group.getMembers()));
        databaseInt.updateGroup(group, "noOfMembers", group.getNoOfMembers());

        // Update user groups
        user.setGroups(group.getGroupID());
        database.updateUserProfile(user, "groups", String.join(",", user.getGroups()));

        deleteInvitation(group.getGroupID(), user);
        return group;
    }

    public Group leaveGroup(Group group, User user){
        // Update group member
        ArrayList<String> members = group.getMembers();
        members.remove(user.getAccountID());
        group.setMembers(members);
        group.setNoOfMembers(group.getNoOfMembers()-1);
        database.updateGroup(group, "members", String.join(",", group.getMembers()));
        databaseInt.updateGroup(group, "noOfMembers", group.getNoOfMembers());
        
        // Update user group
        ArrayList<String> groups = user.getGroups();
        groups.remove(group.getGroupID());
        user.setGroups(groups);
        database.updateUserProfile(user, "groups", String.join(",", user.getGroups()));

        return group;
    }

    public boolean isMember(Group group, User user){
        ArrayList<String> members = group.getMembers();
        for(String x : members){
            if(x.equals(user.getAccountID()))
                return true;
        }
        return false;
    }

    public boolean isGroupAdmin(Group group, User user){
        if(group.getAdminID().equals(user.getAccountID()))
            return true;
        else
            return false;
    }

    public boolean checkInvitation(Group group, User inviter, User invitee){
        ArrayList<String> groupInvitations = invitee.getGroupInvitations();
        for(String x : groupInvitations){
            String[] xArr = x.split(":");
            if(xArr[0].equals(inviter.getAccountID()) && xArr[1].equals(group.getGroupID()))
                return true;
        }
        return false;
    }

    public User cancelInvitation(Group group, User inviter, User invitee){
        ArrayList<String> groupInvitations = invitee.getGroupInvitations();
        for(int i=0; i<groupInvitations.size(); i++){
            String[] xArr = groupInvitations.get(i).split(":");
            if(xArr[0].equals(inviter.getAccountID()) && xArr[1].equals(group.getGroupID())){
                groupInvitations.remove(i);
                invitee.setGroupInvitations(groupInvitations);
                database.updateUserProfile(invitee, "groupInvitations", String.join(",", groupInvitations));
            }
        }
        return invitee;
    }

    public void deleteInvitation(String groupID, User invitee){
        ArrayList<String> groupInvitations = invitee.getGroupInvitations();
        for(int i=0; i<groupInvitations.size(); i++){
            String[] xArr = groupInvitations.get(i).split(":");
            if(xArr[1].equals(groupID)){
                groupInvitations.remove(i);
                database.updateUserProfile(invitee, "groupInvitations", String.join(",", groupInvitations));
            }
        }
    }

    public Group confirmInvitation(Group group, User invitee){
        group = joinGroup(group, invitee);

        return group;
    }
}
