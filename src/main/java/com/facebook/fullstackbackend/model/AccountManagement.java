package com.facebook.fullstackbackend.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.InputMismatchException;
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
        while(!database.confirmPassword(password, retypePassword)){
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
        System.out.print("Email address or phone number: ");
        String emailOrPhoneNo = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.println("*************************");

        // Check if login successful
        while(!database.isLogin(emailOrPhoneNo, password)){
            System.out.print("Email address or phone number: ");
            emailOrPhoneNo = sc.nextLine();
            System.out.print("Password: ");
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
        try{
            int choice = 1;
            while(choice>0){
                System.out.println( "\t" + "\u001B[1m" + user.getName() + "\u001B[0m");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                System.out.println("1 - Posts");
                System.out.println("2 - About");
                System.out.println("3 - Friends");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                switch(choice){
                    case 1: int choicePost = 1;
                            while(choicePost>0){
                                System.out.println("Posts");
                                System.out.println("-------------------------");
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
                                sc.nextLine();
                                System.out.println("*************************");
                                if(choiceAbout==1)
                                    user = connection.editAccount(user);
                            }               
                            break;

                    case 3: int choiceFriend = 1;
                            while(choiceFriend>0){
                                System.out.println("<" + user.getNoOfFriends() + " friends>");
                                System.out.println("-------------------------");
                                System.out.println("0 - Back");
                                System.out.println("1 - My friends");
                                System.out.println("2 - Friend requests");
                                System.out.println("3 - Search friend");
                                System.out.println("*************************");
                                choiceFriend = sc.nextInt();
                                sc.nextLine();
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
        }catch(InputMismatchException e){
            System.out.println("*************************");
            viewMyPage();
        }
    }

    public void viewOtherPage(User u1){
        try{
            int choice = 1;
            while(choice>0){
                // TRUE when is friend, FALSE when not friend
                boolean isFriend = graph.hasEdge(graph, user.getUsername(), u1.getUsername());
                // TRUE when already requested, FALSE when not yet request
                boolean statusRequest = connection.checkRequest(user, u1);

                System.out.println("\t" + "\u001B[1m" + u1.getName() + "\u001B[0m");
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
                sc.nextLine();
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
                                sc.nextLine();
                                System.out.println("*************************");
                            }               
                            break;

                    case 3: int choiceFriend = 1;
                            while(choiceFriend>0){
                                System.out.println("<" + u1.getNoOfFriends() + " friends>");
                            System.out.println("-------------------------");
                                System.out.println("0 - Back");
                                System.out.println("1 - " + u1.getName() + "'s friends");
                                System.out.println("2 - Mutual friends");
                                System.out.println("3 - Search friend");
                                System.out.println("*************************");
                                choiceFriend = sc.nextInt();
                                sc.nextLine();
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
        }catch(InputMismatchException e){
            System.out.println("*************************");
            viewOtherPage(u1);
        }
    }

    public void searchFriend(User u1){
        try{
            int choice = 1;
            while(choice>0){
                choice = 1;
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
                    if(graph.hasEdge(graph, u1.getUsername(), x))
                        result.add(database.getProfile(x));
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
                    System.out.println("No result found.");
                    System.out.println("-------------------------");
                    System.out.println("0 - Back");
                    System.out.println("1 - Search again");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                    if(choice == 1)
                        choice = result.size()+1;
                }

                while(choice>0 && choice<result.size()+1){
                    // Display all search result
                    System.out.println("0 - Back");
                    System.out.println("-------------------------");
                    int count = 1;
                    for(User x : result){
                        String title = "Friend";
                        if(x.getUsername().equals(user.getUsername()))
                            title = "You";
                        System.out.println(count + " - " + x.getName() + " \"" + title + "\"");
                        System.out.println("(" + connection.getTotalMutual(x, user, graph) + " mutuals)");
                        System.out.println("-------------------------");
                        count++;
                    }
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
                        if(result.get(choice-1).getUsername() != user.getUsername())
                            viewOtherPage(result.get(choice-1));
                        else
                            viewMyPage();
                    }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            searchFriend(u1);
        }
    }

    // Display search users and view account of search users
    // Able to send or take back friend request
    public void searchUsers(){
        try{
            int choice = 1;
            while(choice>0){
                choice = 1;     // Initialization
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

                if(result.size()==0){
                    System.out.println("No result found.");
                    System.out.println("-------------------------");
                    System.out.println("0 - Back");
                    System.out.println("1 - Search again");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                    if(choice == 1)
                        choice = result.size()+1;
                }

                while(choice>0 && choice<result.size()+1){
                    // Display all search result
                    System.out.println("0 - Back");
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
            
                    System.out.println("-------------------------");
                    System.out.println(result.size()+1 + " - Search again");
                    System.out.println("-1 - Back to home page");
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
                        if(result.get(choice-1).getUsername() != user.getUsername())
                            viewOtherPage(result.get(choice-1));
                        else
                            viewMyPage();
                    }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            searchUsers();
        }
    }

    public void displayRequest(){
        try{
            ArrayList<User> requestList = database.getRequestList(user);
            System.out.println("<" + requestList.size() + " friend requests>");
            if(requestList.size()==0)
                System.out.println("*************************");
            else
                System.out.println("-------------------------");
            for(int i=0; i<requestList.size(); i++){
                System.out.println(requestList.get(i).getName());
                System.out.println("(" + connection.getTotalMutual(user, requestList.get(i), graph) + " mutuals)");
                System.out.println("-------------------------");
                System.out.println("0 - Next");
                System.out.println("1 - Confirm request");
                System.out.println("2 - Delete request");
                System.out.println("-1 - Back to Friends page");
                System.out.println("*************************");
                int choice = sc.nextInt();
                System.out.println("*************************");

                if(choice<0)
                    break;

                switch(choice){
                    case 0: continue;
                    case 1: graph = connection.confirmRequest(requestList.get(i), user, graph);
                            connection.cancelRequest(requestList.get(i), user);
                            break;
                    case 2: connection.cancelRequest(requestList.get(i), user);
                            break;

                }
            }
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            displayRequest();
        }
    }

    // Method for displayFriends(user) with no arguments
    public void displayFriends(){
        displayFriends(user);
    }

    // Method for displayFriends(anyone) with user object as argument
    public void displayFriends(User u1){
        try{
            ArrayList<String> friends = new ArrayList<>();
            int sortingChoice = 1, choice = 1;
            while(choice>0){
                while(sortingChoice>0 || sortingChoice<0){
                    switch(sortingChoice){
                        case 1 -> friends = connection.displayNewestFriends(user, u1, graph);
                        case 2 -> friends = connection.displayOldestFriends(user, u1, graph);
                    }
                    if(friends.size()!=0 && u1.getUsername().equals(user.getUsername()))
                        System.out.println((friends.size()+1) + " - Sort friend list");
                    System.out.println("0 - Back");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");

                    if(choice==0)
                        sortingChoice = 0;

                    if(choice>0 && choice<friends.size()+1){
                        User friend = database.getProfile(friends.get(choice-1));
                        if(friend.getUsername().equals(user.getUsername()))
                            viewMyPage();
                        else
                            viewOtherPage(friend);
                    }

                    while(choice==friends.size()+1 && choice!=0 && u1.getUsername().equals(user.getUsername())){
                        System.out.println("0 - Back");
                        System.out.println("1 - Newest friends first");
                        System.out.println("2 - Oldest friends first");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");

                        // Condition if input index out of bound
                        if(choice<0 || choice>2){
                            choice = friends.size()+1;
                            continue;
                        }

                        // Condition if choice(1/2) = friends.size(), break loop
                        if(choice==1 || choice==2){
                            sortingChoice = choice;
                            break;
                        }
                    }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            displayFriends(u1);
        }
    }

    public void displayMutual(User u1, User u2, ConnectionGraph<String> graph){
        try{
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
        }catch(InputMismatchException e){
            System.out.println("*************************");
            displayMutual(u1, u2, graph);
        }
    }

    public void displayRecommendedUsers(){
        try{
            ArrayList<User> recomUser = connection.recommendedUser(user, graph);
            for(int i=0; i<recomUser.size(); i++){
                int choice = 4;
                while(choice<-1 || choice>3){
                    // Request from user
                    boolean statusRequest = connection.checkRequest(user, recomUser.get(i));
                    // Request to user
                    boolean pendingRequest = connection.checkRequest(recomUser.get(i), user);
                    System.out.println(recomUser.get(i).getName());
                    System.out.println("(" + connection.getTotalMutual(user, recomUser.get(i), graph) + " mutuals)");
                    System.out.println("-------------------------");
                    if(i<recomUser.size()-1)
                        System.out.println("0 - Next");
                    if(statusRequest)
                        System.out.println("1 - Cancel friend request");
                    else if(pendingRequest)
                        System.out.println("1 - Confirm request");
                    else
                        System.out.println("1 - Add friend");
                    System.out.println("2 - View profile page");
                    if(i>0)
                        System.out.println("3 - Back");
                    System.out.println("-1 - Homepage");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");

                    // Condition if input index out of bound
                    if(choice==0 && i==recomUser.size()-1)
                        choice = 3;
                    
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
                        case 2: viewOtherPage(recomUser.get(i));
                                i--;
                                break;
                        case 3: if(i!=0)
                                    i = i-2;
                                else
                                    i--;
                                break;
                    }
                }
                if(choice<0)
                    break;
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            displayRecommendedUsers();
        }
    }

    public void displayPosts(User u1){
        try{
            ArrayList<Post> yourPosts = database.getUserPosts(u1);
            System.out.println("<" + yourPosts.size() + " posts>");
            System.out.println("-------------------------");
            int choice = 1;
            if(yourPosts.size()==0){
                while(choice!=-1){
                    System.out.println("No posts yet");
                    if(u1.getUsername().equals(user.getUsername()))
                        System.out.println("-1 - Back to posts tab");
                    else
                        System.out.println("-1 - Back to main page");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                }
            }
            
            while(choice>=0){
                for(int i=yourPosts.size()-1; i>=0; i--){
                    yourPosts = database.getUserPosts(u1);
                    if(yourPosts.size()==0){
                        System.out.println("No posts yet");
                        if(u1.getUsername().equals(user.getUsername()))
                            System.out.println("-1 - Back to posts tab");
                        else
                            System.out.println("-1 - Back to main page");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                        break;
                    }
                    ArrayList<String> likeList = database.getPostList(yourPosts.get(i), "likeList");
                    postManager.viewPost(yourPosts.get(i));
                    if(i!=0)
                        System.out.println("0 - Next");
                    else
                        System.out.println("0 - Refresh");
                    boolean likeStatus = false;
                    for(String x : likeList){
                        if(x.equals(user.getUsername())){
                            System.out.println("1 - Unlike");
                            likeStatus = true;
                            break;
                        }
                    }
                    if(!likeStatus)
                        System.out.println("1 - Like");
                    System.out.println("2 - View likes");
                    System.out.println("3 - Comment");
                    System.out.println("4 - View comments");
                    if(i<yourPosts.size()-1)
                        System.out.println("5 - Back");
                    if(user.getUsername()==u1.getUsername())
                        System.out.println("6 - Delete post");
                    // Check if user is viewing his own page or other users page
                    if(u1.getUsername().equals(user.getUsername()))
                        System.out.println("-1 - Back to posts tab");
                    else
                        System.out.println("-1 - Back to main page");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                    if(choice>0){
                        switch(choice){
                            case 1: if(likeStatus)
                                        postManager.unlikePost(yourPosts.get(i), user);
                                    else
                                        postManager.likePost(yourPosts.get(i), user);
                                    break;
                            case 2: postManager.viewLikes(yourPosts.get(i));
                                    break;
                            case 3: postManager.commentPost(yourPosts.get(i), user);
                                    break;
                            case 4: postManager.viewComments(yourPosts.get(i));
                                    break;
                            case 5: i = i+2;
                                    break;
                            case 6: postManager.deletePost(yourPosts.get(i), user);
                                    break;
                        }
                        if(choice==2 || choice==4){
                            System.out.println("0 - Back");
                            System.out.println("*************************");
                            choice = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                            i++;
                        }else if(choice==6){
                            System.out.println("Post successfully deleted");
                            System.out.println("*************************");
                            choice = 0;
                        }else{
                            i++;
                        }
                    }
                    if(choice<0)
                        break;
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            displayPosts(u1);
        }
    }
}
