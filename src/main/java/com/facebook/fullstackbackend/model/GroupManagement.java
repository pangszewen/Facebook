package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    // Method to create new group.
    public User createGroup(User creator){
        try{
            GroupBuilder builder = new GroupBuilder(creator);
            System.out.println("Create Group");
            System.out.println("-------------------------");
            // Enter group name
            System.out.print("Group name: ");
            builder.setGroupName(sc.nextLine());
            System.out.println("*************************");
            // Create Group object
            Group group = builder.build();

            int choice = 2;
            while(choice!=1){
                System.out.println("Invite group members:");
                System.out.println("1 - Continue");     // Continue to create group
                System.out.println("2 - Invite member");    // Creator can invite member before creating group
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                if(choice==2){
                    inviteMember(group, creator);
                }
            }

            choice = 2;     // Initialization
            while(choice!=0 && choice!=1){
                System.out.println("0 - Quit create group");
                System.out.println("1 - Create group");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }

            if(choice==1){
                database.createGroup(group);    // Group created
                chat.createGroupChat(group);    // Group chat created
                // Update the user's groups
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

    // Method to view a group's information.
    public void viewGroupInfo(Group group){
        User admin = database.getProfile(group.getAdminID());
        System.out.println("About Group");
        System.out.println("-------------------------");
        System.out.println("Admin: " + admin.getName());
        System.out.println("No of members: " + group.getNoOfMembers());
        System.out.println("Created on: " + group.getDateOfCreation());
        System.out.println("*************************");
    }

    // Method to edit the group's information. This method can only be accessed by the group's admin.
    public Group editGroupInfo(Group group){
        try{
            String adminID = group.getAdminID();
            int choice = 1;
            while(choice!=0 && adminID.equals(group.getAdminID())){  // This condition is to ensure that the user is still the group's admin.
                System.out.println("Edit Group Info");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                System.out.println("1 - Group name");
                System.out.println("2 - Change admin");     // If the user has passed the group admin role to another member, the user wont be able to edit the group information anymore.
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

    // Method to invite non-member users into the group.
    public void inviteMember(Group group, User inviter){
        try{
            int choice = 1;
            while(choice>0){
                choice = 1;     // Initialization
                System.out.println("Enter search keyword:");
                String emailOrPhoneNoOrUsernameOrName =  sc.nextLine();
                System.out.println("*************************");
                ArrayList<User> searchResult = database.ifContains(emailOrPhoneNoOrUsernameOrName);     // ArrayList of user objects of search result
                ArrayList<User> result = new ArrayList<>();

                for(User x: searchResult){
                    if(!isMember(group, x))    // Filter the searched result to only remain non-member users
                        result.add(x);
                }
                
                // Sort the names alphabetically
                Collections.sort(result, Comparator.comparing(User::getName));
                /*
                for(int i=1; i<result.size(); i++){
                    for(int j=0; j<i; j++){
                        if(result.get(i).getName().compareTo(result.get(j).getName())<0){
                            User temp = result.get(i);
                            result.set(i, result.get(j));
                            result.set(j, temp);
                        }
                    }
                }
                */

                if(result.size()==0){
                    choice = 2;
                    while(choice>1){
                        System.out.println("No result found.");
                        System.out.println("-------------------------");
                        System.out.println("0 - Back");
                        System.out.println("1 - Search again");
                        System.out.println("-1 - Back to Members tab");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                    }
                    if(choice == 1)
                        choice = result.size()+1;   // To skip the while loop below
                }

                while(choice>0 && choice<result.size()+1){
                    // Display all search result
                    System.out.println("0 - Back");
                    for(int i=0; i<result.size(); i++){
                        System.out.println((i+1) + " - " + result.get(i).getName());
                    }
            
                    System.out.println("-------------------------");
                    System.out.println(result.size()+1 + " - Search again");
                    System.out.println("*************************");
                    // Select member-to-be
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");

                    // Condition if selection out of index bound
                    while(choice>result.size()+1){
                        System.out.println("Choice out of bound. Please select again.");
                        choice = sc.nextInt();
                    }

                    // If choice in range, invite or uninvite; else continue
                    if(choice>0 && choice<result.size()+1){
                        int choiceInvite = 1;
                        while(choiceInvite!=0){
                            User u1 = result.get(choice-1);
                            System.out.println(u1.getName());
                            System.out.println("-------------------------");
                            System.out.println("0 - Back");
                            // Check if there is invitation from the user to the selected member-to-be
                            if(checkInvitation(group, inviter, u1))
                                System.out.println("1 - Uninvite");     // User can take back his invitation 
                            else
                                System.out.println("1 - Invite");       // User can send invitation
                            System.out.println("*************************");
                            choiceInvite = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                            if(choiceInvite==1){
                                if(!checkInvitation(group, inviter, u1)){   // User take back invitation
                                    u1.setGroupInvitations(inviter, group);
                                    database.updateUserProfile(u1, "groupInvitations", String.join(",", u1.getGroupInvitations()));
                                }else{                                      // User send invitation
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

    // Method to remove existing member from group. This method can only be accessed by the group's admin.
    public Group removeMember(Group group){
        try{
            int choice = 1;
            while(choice>0){
                choice = 1;     // Initialization
                System.out.println("Enter search keyword:");
                String emailOrPhoneNoOrUsernameOrName =  sc.nextLine();
                System.out.println("*************************");
                ArrayList<User> searchResult = database.ifContains(emailOrPhoneNoOrUsernameOrName);     // ArrayList of user objects of search result
                ArrayList<User> result = new ArrayList<>();

                for(User x: searchResult){
                    if(isMember(group, x))    // Filter the searched result to only remain group members
                        result.add(x);
                }
                
                // Sort the names alphabetically
                Collections.sort(result, Comparator.comparing(User::getName));
                /*
                for(int i=1; i<result.size(); i++){
                    for(int j=0; j<i; j++){
                        if(result.get(i).getName().compareTo(result.get(j).getName())<0){
                            User temp = result.get(i);
                            result.set(i, result.get(j));
                            result.set(j, temp);
                        }
                    }
                }
                */

                if(result.size()==0){
                    choice = 2;
                    while(choice>1){
                        System.out.println("No result found.");
                        System.out.println("-------------------------");
                        System.out.println("0 - Back");
                        System.out.println("1 - Search again");
                        System.out.println("-1 - Back to Members tab");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                    }
                    if(choice == 1)
                        choice = result.size()+1;   // To skip the while loop below
                }

                while(choice>0 && choice<result.size()+1){
                    // Display all search result
                    System.out.println("0 - Back");
                    for(int i=0; i<result.size(); i++){
                        System.out.println((i+1) + " - " + result.get(i).getName());
                    }
            
                    System.out.println("-------------------------");
                    System.out.println(result.size()+1 + " - Search again");
                    System.out.println("*************************");
                    // Select group member
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");

                    // Condition if selection out of index bound
                    while(choice>result.size()+1){
                        System.out.println("Choice out of bound. Please select again.");
                        choice = sc.nextInt();
                    }

                    // If choice in range, selection to remove member; else continue
                    while(choice>0 && choice<result.size()+1){
                        User u1 = result.get(choice-1);
                        System.out.println(u1.getName());
                        System.out.println("-------------------------");
                        System.out.println("0 - Back");
                        System.out.println("1 - Remove member");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                        if(choice==1){
                            if(!isGroupAdmin(group, u1)){   // Condition allowed only for non-admin 
                                group = leaveGroup(group, u1);
                                choice = 0;     // Break loop
                            }else{                          // Admin are not allowed to remove himself from the group
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

    // Method to join group by any user. This method is also used for user to accept group invitations.
    public Group joinGroup(Group group, User user){
        // Increase group members and number of group members
        group.setMembers(user.getAccountID());
        group.setNoOfMembers(group.getNoOfMembers()+1);
        database.updateGroup(group, "members", String.join(",", group.getMembers()));
        databaseInt.updateGroup(group, "noOfMembers", group.getNoOfMembers());

        // Add the group to the user's groups
        user.setGroups(group.getGroupID());
        database.updateUserProfile(user, "groups", String.join(",", user.getGroups()));

        // Delete any invitations to join the group
        deleteInvitation(group.getGroupID(), user);
        return group;
    }

    // Method to leave group by group members.
    public Group leaveGroup(Group group, User user){
        // Decrease group members and number of group members
        ArrayList<String> members = group.getMembers();
        members.remove(user.getAccountID());
        group.setMembers(members);
        group.setNoOfMembers(group.getNoOfMembers()-1);
        database.updateGroup(group, "members", String.join(",", group.getMembers()));
        databaseInt.updateGroup(group, "noOfMembers", group.getNoOfMembers());
        
        // Remove the group from the user's groups
        ArrayList<String> groups = user.getGroups();
        groups.remove(group.getGroupID());
        user.setGroups(groups);
        database.updateUserProfile(user, "groups", String.join(",", user.getGroups()));

        return group;
    }

    // Method to check if a user is the member of the group.
    public boolean isMember(Group group, User user){
        ArrayList<String> members = group.getMembers();
        for(String x : members){
            if(x.equals(user.getAccountID()))
                return true;
        }
        return false;
    }

    // Method to check if a user is the admin of the group.
    public boolean isGroupAdmin(Group group, User user){
        if(group.getAdminID().equals(user.getAccountID()))
            return true;
        else
            return false;
    }

    // Method to check the existence of invitation from a user to a user.
    public boolean checkInvitation(Group group, User inviter, User invitee){
        ArrayList<String> groupInvitations = invitee.getGroupInvitations();
        for(String x : groupInvitations){
            String[] xArr = x.split(":");
            if(xArr[0].equals(inviter.getAccountID()) && xArr[1].equals(group.getGroupID()))
                return true;
        }
        return false;
    }

    // Method to cancel an invitation from a user to a user.
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

    // Method to delete all invitations sent to a user to join a specific group.
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
}
