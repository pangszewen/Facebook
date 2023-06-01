package com.facebook.fullstackbackend.model;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class AccountManagement {
    Scanner sc = new Scanner(System.in);
    UserBuilder builder = new UserBuilder();
    User user;
    ConnectionGraph <String> graph = new ConnectionGraph<>();
    UsersConnection connection = new UsersConnection();
    DatabaseSql<String> database = new DatabaseSql<>();
    PostManagement postManager = new PostManagement();

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
        while(!builder.verifyPassword(password)){
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
        }
        user = database.getProfile(emailOrPhoneNo);

        graph.getGraph(graph);
        System.out.println("\u001B[1m" + user.getName() + "\u001B[0m");
        System.out.println("Welcome to Facebook!");
        System.out.println("*************************");
        return user; 
    }

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
        while(!builder.validateBirthdayFormat(builder.getBirthday())){
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
        System.out.println("What is your gender? (MALE/FEMALE)");
        builder.setGender(sc.nextLine().toUpperCase());

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

    public void viewMyPage(){
        int choice = 1;
        while(choice>0){
            System.out.println("\u001B[1m" + user.getName() + "\u001B[0m");
            System.out.println("-------------------------");
            System.out.println("0 - Back");
            System.out.println("1 - Posts");
            System.out.println("2 - About");
            System.out.println("3 - Friends");
            System.out.println("*************************");
            choice = sc.nextInt();
            System.out.println("*************************");
            switch(choice){
                case 1: int choicePost = 1;
                        while(choicePost>0){
                            System.out.println("0 - Back");
                            System.out.println("1 - Create post");
                            System.out.println("2 - Your posts");
                            System.out.println("*************************");
                            choicePost = sc.nextInt();
                            System.out.println("*************************");
                            switch(choicePost){
                                case 1: postManager.createPost(user);
                                        break;
                                case 2: displayPosts(user);
                                        break;
                            }
                        }
                        break;

                case 2: int choiceAbout = 1;
                        while(choiceAbout>0){
                            connection.viewAccount(user);
                            System.out.println("0 - Back");
                            System.out.println("1 - Edit account");
                            System.out.println("*************************");
                            choiceAbout = sc.nextInt();
                            System.out.println("*************************");
                            if(choiceAbout==1)
                                user = connection.editAccount(user);
                        }               
                        break;

                case 3: System.out.println("<" + user.getNoOfFriends() + " friends>");
                        int choiceFriend = 1;
                        while(choiceFriend>0){
                            System.out.println("0 - Back");
                            System.out.println("1 - My friends");
                            System.out.println("2 - Friend requests");
                            System.out.println("3 - Search friend");
                            System.out.println("*************************");
                            choiceFriend = sc.nextInt();
                            System.out.println("*************************");
                            switch(choiceFriend){
                                case 1 -> displayFriends(user);
                                case 2 -> displayRequest();
                                case 3 -> searchFriend(user);
                            }
                        }
                        break;

            }
        }
    }

    public void viewOtherPage(User u1){
        // TRUE when is friend, FALSE when not friend
        boolean isFriend = graph.hasEdge(graph, user.getUsername(), u1.getUsername());
        // TRUE when already requested, FALSE when not yet request
        boolean statusRequest = connection.checkRequest(user, u1);

        int choice = 1;
        while(choice>0){
            System.out.println("\u001B[1m" + u1.getName() + "\u001B[0m");
            if(isFriend)
                System.out.println("\"Friend\"");
            System.out.println("-------------------------");
            System.out.println("0 - Back");
            System.out.println("1 - Posts");
            System.out.println("2 - About");
            System.out.println("3 - Friends");
            System.out.println("-------------------------");
            if(isFriend)
                System.out.println("4 - Remove friend");    // if they are already friends
            else if(statusRequest = connection.checkRequest(u1, user)){
                System.out.println(u1.getName() + " has requested to be your friend");
                System.out.println("4 - Confirm request");  // if the searched user have sent a friend request to the user
                System.out.println("5 - Delete request");
                isFriend=true;
            }else
                statusRequest = connection.checkRequest(user, u1);

            if(u1.getUsername() != user.getUsername()){
                if(!isFriend){
                    if(statusRequest)
                        System.out.println("4 - Cancel friend request");
                    else
                        System.out.println("4 - Add friend");
                }
            }

            System.out.println("*************************");
            choice = sc.nextInt();
            System.out.println("*************************");
            switch(choice){
                case 1: displayPosts(u1);
                        break;

                case 2: int choiceAbout = 1;
                        while(choiceAbout>0){
                            connection.viewAccount(u1);
                            System.out.println("0 - Back"); 	// Unable to edit other users account
                            System.out.println("*************************");
                            choiceAbout = sc.nextInt();
                            System.out.println("*************************");
                        }               
                        break;

                case 3: System.out.println("<" + u1.getNoOfFriends() + " friends>");
                        int choiceFriend = 1;
                        while(choiceFriend>0){
                            System.out.println("0 - Back");
                            System.out.println("1 - " + u1.getName() + "'s friends");
                            System.out.println("2 - Mutual friends");
                            System.out.println("3 - Search friend");
                            System.out.println("*************************");
                            choiceFriend = sc.nextInt();
                            System.out.println("*************************");
                            switch(choiceFriend){
                                case 1 -> displayFriends(u1);
                                case 2 -> displayMutual(u1, user, graph);
                                case 3 -> searchFriend(u1);
                            }
                        }
                        break;

                case 4: if(isFriend&&!statusRequest)        // user remove friend
                            connection.removeFriend(user, u1, graph);
                        else if(isFriend&&statusRequest)    // user confirm friend request
                            connection.cancelRequest(user, u1);
                        else if(!isFriend&&!statusRequest)  // user send friend request
                            connection.sendRequest(user, u1);
                        else                                // user cancel friend request sent to searched user
                            connection.cancelRequest(user, u1);
                        break;

                case 5: connection.cancelRequest(u1, user); // user delete friend request sent by searched user
                        break;
            }
        }
    }

    public void searchFriend(User u1){
        System.out.println("Enter search keyword:");
        sc.nextLine();
        String emailOrPhoneNoOrUsernameOrName =  sc.nextLine();
        System.out.println("*************************"); 
        ArrayList<User> result = database.ifContains(emailOrPhoneNoOrUsernameOrName);     // ArrayList of user objects of search result

        for(User x : result){
            if(!graph.hasEdge(graph, u1.getUsername(), x.getUsername()))
                result.remove(x);
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
                String title = "Friend";
                if(x.getUsername().equals(user.getUsername()))
                    title = "You";
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
                if(result.get(choice-1).getUsername() != user.getUsername())
                    viewOtherPage(result.get(choice-1));
                else
                    viewMyPage();
            }
        }
    }

    // Display search users and view account of search users
    // Able to send or take back friend request
    public void searchUsers(){
        System.out.println("Press ENTER to start search");
        sc.nextLine();
        System.out.println("Enter search keyword:");
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
                if(result.get(choice-1).getUsername() != user.getUsername())
                    viewOtherPage(result.get(choice-1));
                else
                    viewMyPage();
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

    // Method for displayFriends(user) with no arguments
    public void displayFriends(){
        displayFriends(user);
    }

    // Method for displayFriends(anyone) with user object as argument
    public void displayFriends(User u1){
        int choice = 1;
        while(choice>0){
            ArrayList<String> friends = connection.displayNewestFriends(u1, graph);
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
                    case 1 -> friends = connection.displayNewestFriends(u1, graph);
                    case 2 -> friends = connection.displayOldestFriends(u1, graph);
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
                viewOtherPage(friend);
            }
        }
    }

    public void displayMutual(User u1, User u2, ConnectionGraph<String> graph){
        int choice = 1;
        while(choice>0){
            ArrayList<User> mutual = connection.getMutual(u1, u2, graph);
            // Display appropriate message if have 0 mutual friend
            if(mutual.size()==0){
                System.out.println("No mutual friend");
                System.out.println("0 - Back");
            }else{
                System.out.println("<" + mutual.size() + " mutual friends");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                for(int i=1; i<=mutual.size(); i++){
                    System.out.println(i + " - " + mutual.get(i-1).getName());
                }
            }
            System.out.println("*************************");
            choice = sc.nextInt();
            System.out.println("*************************");
            if(choice>0){
                User friend = mutual.get(choice-1);
                viewOtherPage(friend);
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

    public void displayPosts(User u1){
        ArrayList<Post> yourPosts = database.getUserPosts(u1);
        int choice = 1;
        if(yourPosts.size()==0){
            System.out.println("No posts yet");
            if(u1.getUsername().equals(user.getUsername()))
                System.out.println("-1 - Back to posts tab");
            else
                System.out.println("-1 - Back to main page");
            System.out.println("*************************");
                choice = sc.nextInt();
            System.out.println("*************************");
        }
        while(choice>=0){
            for(int i=yourPosts.size()-1; i>=0; i--){
                System.out.println("0 - Next");
                System.out.println("1 - Like");
                System.out.println("2 - View likes");
                System.out.println("3 - Comment");
                System.out.println("4 - View comments");
                if(i>0)
                    System.out.println("5 - Back");
                System.out.println("-1 - Back to posts tab");
                System.out.println("*************************");
                choice = sc.nextInt();
                System.out.println("*************************");
                switch(choice){
                    case 1 -> postManager.likePost(yourPosts.get(i), u1);
                    case 2 -> postManager.viewLikes(yourPosts.get(i));
                    case 3 -> postManager.commentPost(yourPosts.get(i), u1);
                    case 4 -> postManager.viewComments(yourPosts.get(i));
                    case 5 -> i = i-2;
                }
                if(choice<0)
                    break;
            }
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
}
