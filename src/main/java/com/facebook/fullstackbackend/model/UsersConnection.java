package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class UsersConnection {
    Scanner sc = new Scanner(System.in);
    User user;
    DatabaseSql<String> database = new DatabaseSql<>();
    UserBuilder builder = new UserBuilder();
    ConnectionGraph<String> graph = new ConnectionGraph<>();

    // Add new user as vertex to graph
    public ConnectionGraph<String> registerGraph(ConnectionGraph<String> graph, String username){
        return graph.registerVertex(graph, username);
    }

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
        if(user.getJobs().size()>1){
            System.out.println("Previous job: " + user.getJobs().peek());
        }
        user.getJobs().push(currentJob);
        System.out.println("*************************");            
    }

    public User editAccount(User user){
        int choice = 1;
        while(choice>0){
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
    }

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

    public int getTotalMutual(User u1, User u2, ConnectionGraph<String> graph){
        ArrayList<User> mutual = getMutual(u1, u2, graph);
        return mutual.size();
    }

    // Return ArrayList of recommended users
    // Not used in test class
    public ArrayList<User> recommendedUser(User u1, ConnectionGraph<String> graph){
        ArrayList<User> recomUser = new ArrayList<>();
        ArrayList<Integer> mutualNo = new ArrayList<>();
        ArrayList<String> v = graph.getAllVertices(graph);

        for(int i=0; i<v.size(); i++){
            // Users yet to setup account is not included
            if(database.isSetup(v.get(i))){

                // User himself and his friends are not included in recommendation list
                if(!v.get(i).equals(u1.getUsername()) && !graph.hasEdge(graph, u1.getUsername(), v.get(i))){
                    User u2 = database.getProfile(v.get(i));
                    // Users that have already reqeusted to be friend are not included in recommendation list
                    if(!checkRequest(u1, u2)){
                        int totalMutual = getTotalMutual(u1, u2, graph);
                        recomUser.add(u2);
                        mutualNo.add(totalMutual);
                    }
                }
            }
        }
        for(int i=1; i<mutualNo.size(); i++){
            for(int j=0; j<i; j++){
                if(mutualNo.get(i).compareTo(mutualNo.get(j))>0){
                    int temp1 = mutualNo.get(i);
                    mutualNo.set(i, mutualNo.get(j));
                    mutualNo.set(j, temp1);
                    User temp2 = recomUser.get(i);
                    recomUser.set(i, recomUser.get(j));
                    recomUser.set(j, temp2);
                }
            }
        }
        return recomUser;
    }
    
    // Use in test class to display recommended users
    public User displayRecommendedUsers(User u1, ConnectionGraph<String> graph){
        ArrayList<User> recomUser = recommendedUser(u1, graph);
        for(int i=1; i<=recomUser.size(); i++){
            System.out.print(i + " - " + recomUser.get(i-1).getName());
            System.out.println(" (" + getTotalMutual(u1, recomUser.get(i-1), graph) + ")");
        }
        int choice = sc.nextInt();
        if(choice>0)
            return recomUser.get(choice-1);
        return null;
    }

    // Add friend by sending reqeust
    public void sendRequest(User sender, User receiver){
        ArrayList<User> requestList = database.getRequestList(receiver);
        requestList.add(sender);
        database.updateRequestList(receiver, requestList);
    }

    public boolean checkRequest(User sender, User receiver){
        ArrayList<User> requestList = database.getRequestList(receiver);
        for(User x : requestList){
            if(x.getUsername().compareTo(sender.getUsername())==0)
                return true;
        }
        return false;
    }

    public void cancelRequest(User sender, User receiver){
        ArrayList<User> requestList = database.getRequestList(receiver);
        ArrayList<String> username = new ArrayList<>();
        for(User x : requestList){
            username.add(x.getUsername());
        }
        username.remove(sender.getUsername());
        requestList.clear();
        for(String x : username){
            requestList.add(database.getProfile(x));
        }
        database.updateRequestList(receiver, requestList);
    }

    public ConnectionGraph<String> confirmRequest(User sender, User receiver, ConnectionGraph<String> graph){
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
        return graph;
    }

    public ConnectionGraph<String> removeFriend(User sender, User receiver, ConnectionGraph<String> graph){
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

    // Display newest friends at top (ArrayList-loop from head of ArrayList)
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
            if(!u.getUsername().equals(user.getUsername()))
            System.out.println("(" + getTotalMutual(user, u, graph) + " mutuals)");
            System.out.println("-------------------------");
        }
        return friends;
    }

    // Display oldest friends at top (ArrayList-loop from tail of ArrayList)
    public ArrayList<String> displayOldestFriends(User user, User u1, ConnectionGraph<String> graph){
        ArrayList<String> friends = graph.getNeighbours(graph, u1.getUsername());
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
            if(!u.getUsername().equals(user.getUsername()))
                System.out.println("(" + getTotalMutual(user, u, graph) + " mutuals)");
            System.out.println("-------------------------");
        }
        return friends;
    }
}
