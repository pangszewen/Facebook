package com.facebook.fullstackbackend.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class AccountManagement {
    Scanner sc = new Scanner(System.in);
    UserBuilder builder = new UserBuilder();
    User user;
    ConnectionGraph <String> graph = new ConnectionGraph<>();
    UsersConnection connection = new UsersConnection();
    DatabaseSql<String> database = new DatabaseSql<>();

    public AccountManagement(){}

    public void registration(){
        System.out.println("\tRegistration Form");
        System.out.println("-------------------------");

        // Input username
        System.out.print("Username: ");
        String username = sc.nextLine();
        while(!database.verifyUsername(username)){
            System.out.print("Username: ");
            username = sc.nextLine();
        }
        builder.setUsername(username);

        // Input email
        System.out.print("Email: ");
        String email = sc.nextLine();
        while(!database.verifyEmail(email)){
            System.out.print("Email: ");
            email = sc.nextLine();
        }
        builder.setEmail(email);

        // Input phone number
        System.out.print("Phone number: ");
        String phoneNo = sc.nextLine();
        while(!database.verifyPhoneNo(phoneNo)){
            System.out.print("Phone number: ");
            phoneNo = sc.nextLine();
        }
        builder.setPhoneNo(phoneNo);

        // Input password
        System.out.print("Password: ");
        String password = sc.nextLine();
        while(!verifyPassword(password)){
            System.out.print("Password: ");
            password = sc.nextLine();
        }
        // Retype password
        System.out.print("Confirm password: ");
        String retypePassword = sc.nextLine();
        while(!confirmPassword(password, retypePassword)){
            System.out.print("Confirm password: ");
            retypePassword = sc.nextLine();
        }
        builder.setPassword(password);

        // Get account ID
        builder.setAccountID();
        //builder = new UserBuilder(builder.getAccountID(), username, email, phoneNo, password, null);

        // Get user
        user = builder.build();
        
        // Store user info into CSV file
        database.registerUser(user);

        // Add user to graph 
        connection.registerGraph(graph, username);
        System.out.println("*************************");
    }

    public User login(){
        System.out.println("\tLogin Page");
        System.out.println("-------------------------");
        System.out.print("Enter your email address or phone number: ");
        String emailOrPhoneNo = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.println("*************************");

        // Check if login successful
        while(!database.isLogin(emailOrPhoneNo, password)){
            System.out.print("Reenter your email address or phone number: ");
            emailOrPhoneNo = sc.nextLine();
            System.out.print("Reenter password: ");
            password = sc.nextLine();
            System.out.println("*************************");
        }

        // Check if user has setup account
        if(!database.isSetup(emailOrPhoneNo)){
            user = database.getAccount(emailOrPhoneNo);
            user = setupAccount(user);
            database.setupProfile(user);
            user = database.getProfile(emailOrPhoneNo);
        }

        user = database.getProfile(emailOrPhoneNo);

        graph.getGraph(graph);
        System.out.println(user.getName());
        System.out.println("Welcome to Facebook!");
        System.out.println("*************************");
        return user; 
    }

    public void viewAccount(User user){
        String accountID = user.getAccountID();
        String username = user.getUsername();
        String email = user.getEmail();
        String phoneNo = user.getPhoneNo();
        String role = user.getRole();
        String name = user.getName();
        String birthday = user.getBirthday();
        int age = user.getAge();
        String address = user.getAddress();
        char gender = user.getGender();
        String status = user.getStatus();
        int noOfFriends = user.getNoOfFriends();
        ArrayList<String> hobbies = user.getHobbies();
        Stack<String> jobs = user.getJobs();

        System.out.println("\tAccount Info");
        System.out.println("-------------------------");
        System.out.println("Account ID: " + accountID);
        System.out.println("Username: " + username);
        System.out.println("Email address: " + email);
        System.out.println("Phone number: " + phoneNo);
        System.out.println("Role: " + role);
        System.out.println("Name: " + name);
        System.out.println("Birthday: " + birthday);
        System.out.println("Age: " + age);
        System.out.println("Address: " + address);
        System.out.println("Gender: " + gender);
        System.out.println("Relationship status: " + status);
        System.out.println("Number of friends: " + noOfFriends);
        System.out.println("Hobbies: ");
        for(int i=0; i<hobbies.size(); i++){
            System.out.println(hobbies.get(i));
        }
        String currentJob = jobs.pop();
        System.out.println("Current job: " + currentJob);
        if(jobs.size()>1){
            System.out.println("Previous job: " + jobs.peek());
        }
        jobs.push(currentJob);
        System.out.println("*************************");
    }

    // Check whether user has setup profile 
    /*
    public boolean isSetup(User user){
        if(user.getName()==null)
            return false;
        return true;
    }
    */

    public User setupAccount(User user){
        builder.setAccountID(user.getAccountID());
        builder.setUsername(user.getUsername());
        builder.setEmail(user.getEmail());
        builder.setPhoneNo(user.getPhoneNo());
        builder.setPassword(user.getPassword());
        builder.setRole(user.getRole());

        System.out.println("Please fill in the below information to get started!");

        // Input name
        System.out.println("What is your name?");
        builder.setName(sc.nextLine());

        //Input birthday
        System.out.println("When is your birthday? (format: YYYY-MM-DD)");
        builder.setBirthday(sc.nextLine());
        //Check validation of birthday format
        while(!validateBirthdayFormat(builder.getBirthday())){
            System.out.println("When is your birthday? (format: YYYY-MM-DD)");
            builder.setBirthday(sc.nextLine());
        }
        LocalDate birthday = LocalDate.parse(builder.getBirthday());

        // Calculate age based on birthday
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthday, currentDate);
        builder.setAge(period.getYears());

        // Get address
        System.out.println("Where do you live?");
        builder.setAddress(sc.nextLine());

        // Get gender
        System.out.println("What is your gender? (M-male, F-female, N-not applicable)");
        builder.setGender(sc.next().charAt(0));
        sc.nextLine();

        // Initialize number of friends
        builder.setNoOfFriends(0);

        // Get relationship status
        System.out.println("What is your relationship status?");
        builder.setStatus(sc.nextLine());

        // Get hobbies (ArrayList)
        System.out.println("What are your hobbies?");
        builder.setHobbies(sc.nextLine());
        System.out.print("Do you wish to add more hobbies? (y-yes, n-no)");
        char choice = sc.next().charAt(0);
        sc.nextLine();
        while(choice=='y'){
            builder.setHobbies(sc.nextLine());        
            System.out.print("Do you wish to add more hobbies? (y-yes, n-no)");
            choice = sc.next().charAt(0);
            sc.nextLine();
        }

        // Get job (Stack)
        System.out.println("What is your current job?");
        builder.setJobs(sc.nextLine());     

        // Done setup account
        System.out.println("That's all for the account setup. You are now ready to explore Facebook!");
        System.out.println("*************************");
        return builder.build();
    }

    // Display search friend and view account of search friend
    // Able to send or take back friend request
    public void searchFriend(){
        System.out.println("Enter search keyword:");
        sc.nextLine();
        String emailOrPhoneNoOrUsernameOrName =  sc.nextLine();
        System.out.println("*************************");
        ArrayList<User> result = database.ifContains(emailOrPhoneNoOrUsernameOrName);     // ArrayList of user objects of search result

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

        int choice = 1;
        if(result.size()==0){
            System.out.println("No result found.");
            choice = 0;
            System.out.println("*************************");
        }
        while(choice>0){
            // Display all search result
            int count = 1;
            for(User x : result){
                String title = "New";
                if(x.getUsername().equals(user.getUsername()))
                    title = "You";
                else if(graph.hasEdge(graph, user.getUsername(), x.getUsername()))
                    title = "Friend";
                System.out.println(count + " - " + x.getName() + " \"" + title + "\"");
                count++;
            }
            
            // Select to view searched account
            System.out.println("Enter 0 to exit.");
            System.out.println("*************************");
            choice = sc.nextInt();
            System.out.println("*************************");

            // Condition if selection out of index bound
            while(choice>result.size()){
                System.out.println("Choice out of bound. Please select again.");
                choice = sc.nextInt();
            }

            // If choice in range, view account; else continue
            if(choice>0){
                viewAccount(result.get(choice-1));

                // TRUE when is friend, FALSE when not friend
                boolean isFriend = graph.hasEdge(graph, user.getUsername(), result.get(choice-1).getUsername());
                // TRUE when already requested, FALSE when not yet request
                boolean statusRequest = connection.checkRequest(user, result.get(choice-1));
                System.out.println("1 - Back");
                if(isFriend)
                    System.out.println("2 - Remove friend");    // if they are already friends
                else if(statusRequest = connection.checkRequest(result.get(choice-1), user)){
                    System.out.println("2 - Confirm request");  // if the searched user have sent a friend request to the user
                    System.out.println("3 - Delete request");
                    isFriend=true;
                }else
                    statusRequest = connection.checkRequest(user, result.get(choice-1));

                if(result.get(choice-1).getUsername() != user.getUsername()){
                    if(!isFriend){
                        if(statusRequest)
                            System.out.println("2 - Cancel friend request");
                        else
                            System.out.println("2 - Add friend");
                    }
                }else{
                    System.out.println("2 - Edit account");
                }
                System.out.println("*************************");
                int choiceToAdd = sc.nextInt();
                System.out.println("*************************");
                if(choiceToAdd==2){
                    if(result.get(choice-1).getUsername() != user.getUsername()){
                        if(isFriend&&!statusRequest)        // user remove friend
                            connection.removeFriend(user, result.get(choice-1), graph);
                        else if(isFriend&&statusRequest){  // user confirm friend request
                            connection.cancelRequest(user, result.get(choice-1));
                        }else if(!isFriend&&!statusRequest) // user send friend request
                            connection.sendRequest(user, result.get(choice-1));
                        else                                // user cancel friend request sent to searched user
                            connection.cancelRequest(user, result.get(choice-1));
                    } 
                    // Add edit account method                        
                }else{  // user delete friend request sent by searched user
                    connection.cancelRequest(result.get(choice-1), user);
                }
            }
        }
    }

    public void displayRequest(){
        ArrayList<User> requestList = database.getRequestList(user);
        for(int i=0; i<requestList.size(); i++){
            System.out.println(requestList.get(i).getName());
            System.out.println("(" + connection.getTotalMutual(user, requestList.get(i), graph) + " mutuals)");
            System.out.println("0 - Next");
            System.out.println("1 - Confirm request");
            System.out.println("2 - Delete request");
            System.out.println("*************************");
            int choice = sc.nextInt();
            System.out.println("*************************");
            switch(choice){
                case 0: continue;
                case 1: graph = connection.confirmRequest(requestList.get(i), user, graph);
                        connection.cancelRequest(requestList.get(i), user);
                        break;
                case 2: connection.cancelRequest(requestList.get(i), user);
                        break;

            }
        }
    }

    public void displayFriends(){
        int choice = 1;
        while(choice>0){
            ArrayList<String> friends = connection.displayNewestFriends(user, graph);
            System.out.println("-------------------------");
            System.out.println("-1 - Sort friend list");
            System.out.println("0 - Back");
            System.out.println("*************************");
            choice = sc.nextInt();
            System.out.println("*************************");
            while(choice<0){
                System.out.println("1 - Newest friends first");
                System.out.println("2 - Oldest friends first");
                System.out.println("*************************");
                int sortingChoice = sc.nextInt();
                System.out.println("*************************");
                switch(sortingChoice){
                    case 1 -> friends = connection.displayNewestFriends(user, graph);
                    case 2 -> friends = connection.displayOldestFriends(user, graph);
                }
                System.out.println("-------------------------");
                System.out.println("-1 - Sort friend list");
                System.out.println("0 - Back");
                System.out.println("*************************");
                choice = sc.nextInt();
                System.out.println("*************************");

            }
            if(choice>0){
                User friend = database.getProfile(friends.get(choice-1));
                viewAccount(friend);
                System.out.println("0 - Back");
                System.out.println("1 - Remove friend");
                int removeStatus = sc.nextInt();
                System.out.println("*************************");
                if(removeStatus==1)
                    graph = graph.removeUndirectedEdge(graph, user.getUsername(), friend.getUsername());
            }
        }
    }

    public void displayRecommendedUsers(){
        ArrayList<User> recomUser = connection.recommendedUser(user, graph);
        for(int i=0; i<recomUser.size(); i++){
            // Request from user
            boolean statusRequest = connection.checkRequest(user, recomUser.get(i));
            // Request to user
            boolean pendingRequest = connection.checkRequest(recomUser.get(i), user);
            System.out.println(recomUser.get(i).getName());
            System.out.println("(" + connection.getTotalMutual(user, recomUser.get(i), graph) + " mutuals)");
            System.out.println("0 - Next");
            if(statusRequest)
                System.out.println("1 - Cancel friend request");
            else if(pendingRequest)
                System.out.println("1 - Confirm request");
            else
                System.out.println("1 - Add friend");
            if(i>0)
                System.out.println("2 - Back");
            System.out.println("-1 - Homepage");
            System.out.println("*************************");
            int choice = sc.nextInt();
            System.out.println("*************************");
            switch(choice){
                case 0: continue;
                case 1: if(statusRequest)
                            connection.cancelRequest(user, recomUser.get(i));
                        else if(pendingRequest){
                            graph = connection.confirmRequest(recomUser.get(i), user, graph);
                            connection.cancelRequest(recomUser.get(i), user);
                        }else
                            connection.sendRequest(user, recomUser.get(i));
                        break;
                case 2: i = i-2;
                        break;
            }
            if(choice<0)
                break;
        }
    }

    public boolean verifyPassword(String password){
        // Check password length
        if(password.length()<8){
            System.out.println("Password must be at least 8 characters.");
            return false;
        }

        // Check if password contains space characters
        if(password.contains(" ")){
            System.out.println("Password must not contain any space.");
            return false;
        }

        // Check if password is a strong password
        boolean upper=false, lower=false, digit=false, special=false;
        for(int i=0; i<password.length(); i++){
            char ch = password.charAt(i);
            if((int)ch >= (int)'A' && (int)ch <= (int)'Z')
                upper = true;
            else if((int)ch >= (int)'a' && (int)ch <= (int)'z')
                lower = true;
            else if((int)ch >= (int)'0' && (int)ch <= (int)'9')
                digit = true;
            else if((int)ch>=32&&(int)ch<=47 || (int)ch>=58&&(int)ch<=64 || (int)ch>=91&&(int)ch<=96 || (int)ch>=123&&(int)ch<=126)
                special = true;

        }
        if(upper&&lower&&digit&&special)
            return true;
        else{
            if(!upper)
                System.out.println("Password must contains at least one uppercase letter.");
            if(!lower)
                System.out.println("Password must contains at least one lowercase letter.");
            if(!digit)
                System.out.println("Password must contains at least one digit");
            if(!special)
                System.out.println("Password must contains at least one special character.");
            return false;
        }
    }
    
    public boolean confirmPassword(String p1, String p2){
        if(p1.equals(p2))
            return true;
        else{
            System.out.println("Password is not matched. Please try again.");
            return false;
        }
    }

    public boolean validateBirthdayFormat(String birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        try {
            LocalDate.parse(birthday, formatter);
            return true; // Format is valid
        } catch (DateTimeParseException e) {
            System.out.println("Invalid birthday format");
            return false; // Format is invalid
        }
    }
}
