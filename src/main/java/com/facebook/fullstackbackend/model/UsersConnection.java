package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class UsersConnection {
    Scanner sc = new Scanner(System.in);
    User user;
    DatabaseSql<String> database = new DatabaseSql<>();
    UserBuilder builder = new UserBuilder();
    ConnectionGraph<String> graph = new ConnectionGraph<>();
    Chat chat = new Chat();

    // Method to add new user as vertex to graph.
    public ConnectionGraph<String> registerGraph(ConnectionGraph<String> graph, String username){
        return graph.registerVertex(graph, username);
    }

    // Method to view a user's account details.
    public void viewAccount(User user){
        System.out.println("\tAbout");
        System.out.println("-------------------------");
        System.out.println("Basic info");
        System.out.println("-------------------------");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Gender: " + user.getGender());
        System.out.println("Birthday: " + user.getBirthday());
        System.out.println("Age: " + user.getAge());
        System.out.println("-------------------------");
        System.out.println("Contact info");
        System.out.println("-------------------------");
        System.out.println("Email address: " + user.getEmail());
        System.out.println("Phone number: " + user.getPhoneNo());
        System.out.println("-------------------------");
        System.out.println("Other info");
        System.out.println("-------------------------");
        System.out.println("Relationship status: " + user.getStatus());
        System.out.println("Address: " + user.getAddress());
        System.out.println("Hobby: " + user.getHobbies().get(0));
        String currentJob = user.getJobs().pop();
        System.out.println("Current job: " + currentJob);
        if(user.getJobs().size()>=1){    // Display previous job if available.
            System.out.println("Previous job: " + user.getJobs().peek());
        }
        user.getJobs().push(currentJob);
        System.out.println("*************************");            
    }

    // Method to edit account details by user.
    public User editAccount(User user){
        try{
            int choice = 1;
            while(choice!=0){
                System.out.println("Edit Account");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                System.out.println("1 - Password");
                System.out.println("2 - Name");
                System.out.println("3 - Birthday");
                System.out.println("4 - Address");
                System.out.println("5 - Gender");
                System.out.println("6 - Relationship status");
                System.out.println("7 - Hobby");
                System.out.println("8 - Jobs");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                switch(choice){
                    case 1 -> user = builder.editPassword(user);
                    case 2 -> user = builder.editName(user);
                    case 3 -> user = builder.editBirthday(user);
                    case 4 -> user = builder.editAddress(user);
                    case 5 -> user = builder.editGender(user);
                    case 6 -> user = builder.editStatus(user);
                    case 7 -> user = builder.editHobbies(user);
                    case 8 -> user = builder.editJobs(user);
                }
                System.out.println("*************************");
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editAccount(user);
        }
        return user;
    }

    // Method to get the mutual friends between two users.
    public ArrayList<User> getMutual(User u1, User u2, ConnectionGraph<String> graph){
        ArrayList<User> mutual = new ArrayList<>();
        String v1 = graph.getVertex(graph, u1.getUsername());
        ArrayList<String> n1 = graph.getNeighbours(graph, v1);
        String v2 = graph.getVertex(graph, u2.getUsername());
        ArrayList<String> n2 = graph.getNeighbours(graph, v2);
        for(int i=0; i<n1.size(); i++){
            for(int j=0; j<n2.size(); j++){
                if(n1.get(i).equals(n2.get(j))){
                    mutual.add(database.getProfile(n2.get(j)));
                    continue;
                }
            }
        }
        return mutual;
    }

    // Method to get the total number of mutual friends between two users.
    public int getTotalMutual(User u1, User u2, ConnectionGraph<String> graph){
        ArrayList<User> mutual = getMutual(u1, u2, graph);
        return mutual.size();
    }

    // Method to get the recommended users.
    public ArrayList<User> recommendedUser(User user, ConnectionGraph<String> graph){
        ArrayList<User> recomUser = new ArrayList<>();
        ArrayList<String> v = graph.getAllVertices(graph);

        for(int i=0; i<v.size(); i++){
            // Users yet to setup account is not included
            if(database.isSetup(v.get(i))){

                // User himself and his friends are not included in recommendation list
                if(!v.get(i).equals(user.getUsername()) && !graph.hasEdge(graph, user.getUsername(), v.get(i))){
                    User u1 = database.getProfile(v.get(i));
                    // Users that have already reqeusted to be friend are not included in recommendation list
                    if(!checkRequest(user, u1)){
                        recomUser.add(u1);
                    }
                }
            }
        }
        for(int i=1; i<recomUser.size(); i++){
            int mutualNo1 = getTotalMutual(user, recomUser.get(i), graph);
            for(int j=0; j<i; j++){
                int mutualNo2 = getTotalMutual(user, recomUser.get(j), graph);
                if(mutualNo1>mutualNo2){
                    User temp = recomUser.get(i);
                    recomUser.set(i, recomUser.get(j));
                    recomUser.set(j, temp);
                }
            }
        }
        return recomUser;
    }

    // Method to add friend by sending reqeust.
    public void sendRequest(User sender, User receiver){
        Stack<String> requestList = receiver.getRequestList();
        requestList.add(sender.getUsername());
        database.updateUserProfile(receiver, "requestList", String.join(",", requestList));
    }

    // Method to check the existence of friend request from a user to a user.
    public boolean checkRequest(User sender, User receiver){
        Stack<String> requestList = receiver.getRequestList();
        for(String x : requestList){
            if(x.compareTo(sender.getUsername())==0)
                return true;
        }
        return false;
    }

    // Method to cancel friend request from a user to a user.
    public void cancelRequest(User sender, User receiver){
        Stack<String> requestList = receiver.getRequestList();
        requestList.remove(sender.getUsername());
        database.updateUserProfile(receiver, "requestList", String.join(",", requestList));
    }

    // Method to confirm friend request.
    public ConnectionGraph<String> confirmRequest(User sender, User receiver, ConnectionGraph<String> graph){
        // Update graph by adding new undirected edge
        graph = graph.addUndirectedEdge(graph, sender.getUsername(), receiver.getUsername());

        // Increment number of friends
        sender.setNoOfFriends(sender.getNoOfFriends()+1);
        receiver.setNoOfFriends(receiver.getNoOfFriends()+1);
        // Update no of friends
        DatabaseSql<Integer> database = new DatabaseSql<>();
        database.updateUserProfile(sender, "noOfFriends", sender.getNoOfFriends());
        database.updateUserProfile(receiver, "noOfFriends", receiver.getNoOfFriends());

        // Remove friend request from receiver's request list
        cancelRequest(sender, receiver);

        //Create chat
        if(!chat.verifyUserChatExist(sender, receiver))
            chat.createUserChat(sender, receiver);

        return graph;
    }

    // Method to remove friend.
    public ConnectionGraph<String> removeFriend(User sender, User receiver, ConnectionGraph<String> graph){
        // Update graph by removing undirected edge
        graph = graph.removeUndirectedEdge(graph, sender.getUsername(), receiver.getUsername());

        // Decrement number of friends
        sender.setNoOfFriends(sender.getNoOfFriends()-1);
        receiver.setNoOfFriends(receiver.getNoOfFriends()-1);
        // Update no of friends
        DatabaseSql<Integer> database = new DatabaseSql<>();
        database.updateUserProfile(sender, "noOfFriends", sender.getNoOfFriends());
        database.updateUserProfile(receiver, "noOfFriends", receiver.getNoOfFriends());

        return graph;
    }

    // Method to display newest friends at top (ArrayList-loop from head of ArrayList)
    public ArrayList<String> displayNewestFriends(User user, User u1, ConnectionGraph<String> graph){
        ArrayList<String> friends = graph.getNeighbours(graph, u1.getUsername());
        System.out.println("<" + friends.size() + " friends>");
        System.out.println("-------------------------");
        for(int i=1; i<=friends.size(); i++){
            User u = database.getProfile(friends.get(i-1));
            String title = "New";
            if(u.getUsername().equals(user.getUsername()))
                title = "You";
            else if(graph.hasEdge(graph, user.getUsername(), u.getUsername()))
                title = "Friend";
            System.out.println(i + " - " + u.getName() + " \"" + title + "\"");
            // User has no mutual friends with himself.
            if(!u.getUsername().equals(user.getUsername()))
                System.out.println("(" + getTotalMutual(user, u, graph) + " mutuals)");
            System.out.println("-------------------------");
        }
        return friends;
    }

    // Method to display oldest friends at top (ArrayList-reverse the ArrayList)
    public ArrayList<String> displayOldestFriends(User user, User u1, ConnectionGraph<String> graph){
        ArrayList<String> friends = graph.getNeighbours(graph, u1.getUsername());
        // ArrayList is reversed.
        Collections.reverse(friends);
        System.out.println("<" + friends.size() + " friends>");
        System.out.println("-------------------------");
        for(int i=1; i<=friends.size(); i++){
            User u = database.getProfile(friends.get(i-1));
            String title = "New";
            if(u.getUsername().equals(user.getUsername()))
                title = "You";
            else if(graph.hasEdge(graph, user.getUsername(), u.getUsername()))
                title = "Friend";
            System.out.println(i + " - " + u.getName() + " \"" + title + "\"");
            // User has no mutual friends with himself.
            if(!u.getUsername().equals(user.getUsername()))
                System.out.println("(" + getTotalMutual(user, u, graph) + " mutuals)");
            System.out.println("-------------------------");
        }
        return friends;
    }

    // Method to display friend requests to the user.
    public ConnectionGraph<String> displayRequest(User user, ConnectionGraph<String> graph){
        try{
            int choice = 1;
            while(choice!=0){
                Stack<String> requestList = user.getRequestList();
                Stack<String> temp = new Stack<>();
                temp.addAll(requestList);
                while(!temp.isEmpty()){
                    if (!database.verifyUserExist(temp.peek())) {
                        requestList.remove(temp.peek());
                        database.updateUserProfile(user, "requestList", String.join(",", requestList));
                        temp.pop();
                    }else
                        temp.pop();
                }

                System.out.println("<" + requestList.size() + " friend requests>");
                System.out.println("-------------------------");

                // Condition if there is no friend requests
                if(requestList.size()==0){
                    System.out.println("0 - Back");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    System.out.println("*************************");
                }

                for(int i=0; i<requestList.size(); i++){
                    User u1 = database.getProfile(requestList.get(i));
                    System.out.println(u1.getName());
                    System.out.println("(" + getTotalMutual(user, u1, graph) + " mutuals)");
                    System.out.println("-------------------------");
                    if(i!=requestList.size()-1)
                        System.out.println("0 - Next");
                    System.out.println("1 - Confirm request");
                    System.out.println("2 - Delete request");
                    System.out.println("-1 - Back to Friends page");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    System.out.println("*************************");

                    if(choice<0){   
                        choice=0;   // To break loop
                        break;
                    }

                    switch(choice){
                        case 0: if(i!=requestList.size()-1)
                                    continue;
                                else 
                                    i--;
                                break;
                        case 1: graph = confirmRequest(u1, user, graph);
                                break;
                        case 2: cancelRequest(u1, user);
                                break;
                    }
                }
            }
            return graph;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayRequest(user, graph);
        }
        System.out.println("Failed to display friend requests");
        return graph;
    }

    // Method to start chat with other users.
    public void startUserChatting(User user, User u1){
        System.out.println(u1.getName());
        System.out.println("-------------------------");
        chat.readUserChat(user, u1);
        System.out.println("-------------------------");
        
        chat.updateUserChat(user, u1);
        System.out.println("*************************");
    }

    // Method to start chat in user's group.
    public void startGroupChatting(User user, Group group){
        System.out.println(group.getGroupName());
        System.out.println("-------------------------");
        chat.readGroupChat(group);
        System.out.println("-------------------------");
        chat.updateGroupChat(user, group);
        System.out.println("*************************");
    }
}
