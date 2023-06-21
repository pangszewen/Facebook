package com.facebook.fullstackbackend.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

// The methods in this class is exclusive for admin users only
public class Admin {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
    UserBuilder builder = new UserBuilder();

    // Method to set normal user as admin of Facebook.
    public User setAdmin(User user){
        user.setRole("admin");
        database.updateUserAccount(user, "role", user.getRole());
        database.updateUserProfile(user, "role", user.getRole());
        System.out.println("***" + user.getName() + " is now an admin***");
        System.out.println("*************************");
        return user;
    }

    // Method to check if the given user is an admin
    public boolean isAdmin(User user){
        if(user.getRole().equalsIgnoreCase("admin"))
            return true;
        else
            return false;
    }

    // Method to delete a user from Facebook.
    public ConnectionGraph<String> deleteAccount(User user, ConnectionGraph<String> graph){
        if(isAdmin(user)){
            System.out.println("***This user is an admin. Operation denied***");
            System.out.println("*************************");
            return graph;
        }
        // Remove all user posts
        removeAllUserPost(user);
        // Remove user from all groups
        removeUserFromAllGroups(user);
        // Remove user from graph 
        graph.removeVertex(graph, user.getUsername());
        // Delete user from database
        database.deleteAccount(user);

        return graph;
    }

     // Method used to remove user from all of his groups due to his account has been deleted from Facebook by admin.
    public void removeUserFromAllGroups(User user){
        ArrayList<String> groups = user.getGroups();
        for(String x : groups){
            Group group = database.getGroup(x);
            if(!group.getAdminID().equals(user.getAccountID())){
                ArrayList<String> members = group.getMembers();
                members.remove(user.getAccountID());
                group.setMembers(members);
                group.setNoOfMembers(group.getNoOfMembers()-1);
                database.updateGroup(group, "members", String.join(",", group.getMembers()));
                DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
                databaseInt.updateGroup(group, "noOfMembers", group.getNoOfMembers());
            }else{
                database.deleteGroup(group);
            }
        }
    }

    // Method used to remove all posts created by user.
    public void removeAllUserPost(User user){
        ConnectionGraph<String> graph = new ConnectionGraph<>();
        graph = graph.getGraph(graph);
        ArrayList<Post> posts = database.getUserPosts(user, user, graph);
        for(Post x : posts){
            database.deletePost(x);
        }
    }

    // Method to ban user from using Facebook for a specific duration.
    public void banUser(User user) {
        // Banning admin users are not allowed
        if(isAdmin(user)){
            System.out.println("***This user is an admin. Operation denied***");
            System.out.println("*************************");
            return;
        }
        int choice = 6;
        while(choice<0 || choice>5){
            System.out.println("Select ban duration:");
            System.out.println("1 - One week");
            System.out.println("2 - One month");
            System.out.println("3 - Three months");
            System.out.println("4 - Six months");
            System.out.println("5 - One year");
            System.out.println("0 - Back");
            System.out.println("*************************");
            choice = sc.nextInt();
            sc.nextLine();
            System.out.println("*************************");
            Period duration = null;
            switch(choice){
                case 1 -> duration = Period.ofWeeks(1);
                case 2 -> duration = Period.ofMonths(1); 
                case 3 -> duration = Period.ofMonths(3);
                case 4 -> duration = Period.ofMonths(6);
                case 5 -> duration = Period.ofYears(1);
            }
            if(choice>0 && choice<6){
                user.setBanDuration(String.valueOf(duration));
                user.setBanStartDate(String.valueOf(LocalDate.now()));
                database.updateUserProfile(user, "banDuration", user.getBanDuration());
                database.updateUserProfile(user, "banStartDate", user.getBanStartDate());
            }
        }
    }

    public void unbanUser(User user){
        user.setBanDuration("P0Y0M0D");
        user.setBanStartDate("");
        database.updateUserProfile(user, "banDuration", user.getBanDuration());
        database.updateUserProfile(user, "banStartDate", user.getBanStartDate());
    }

    // Content is manually removed by the admin
    public void manuallyRemoveInappropriateContent(Post post, User user){        
        database.deletePost(post);
        user.setNoOfDeletedPost(user.getNoOfDeletedPost()+1);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updateUserProfile(user, "noOfDeletedPost", user.getNoOfDeletedPost());
    }

    // Method to add new prohibited words into database.
    public void updateProhibitedWord(){
        try{
            int choice = 2;
            while(choice>0){
                choice = 2;     // Initialization
                boolean status = false;
                ArrayList<String> prohibitedWords = database.getProhibitedWords();
                displayProhibitedWord(prohibitedWords);
                System.out.print("Enter new prohibited words: ");
                String newWord = sc.nextLine();
                System.out.println("-------------------------");
                for(String x : prohibitedWords){
                    if(newWord.contains(x)){    // If the new word is repeated, the new word wont be added.
                        System.out.println("This word is already included in this prohibited word: " + x);
                        System.out.println("*************************");
                        while(choice>1 || choice<0){
                            System.out.println("0 - Back");
                            System.out.println("1 - Continue to add");
                            System.out.println("*************************");
                            choice = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                        }
                        status = true;
                        break;
                    }
                }
                if(!status){
                    database.addNewProhibitedWord(newWord);
                    System.out.println("Added prohibited word: " + newWord);
                    System.out.println("*************************");
                    while(choice>1 || choice<0){
                        System.out.println("0 - Back");
                        System.out.println("1 - Continue to add");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                    }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            updateProhibitedWord();
        }
    }

    // Method to display all the prohibited words as reference for admin.
    public void displayProhibitedWord(ArrayList<String> prohibitedWords){
        System.out.println("<" + prohibitedWords.size() + " prohibited words>");
        System.out.println("-------------------------");
        for(int i=0; i<prohibitedWords.size(); i++){
            System.out.println((i+1) + " - " + prohibitedWords.get(i));
        }
        System.out.println("*************************");
    }

}
