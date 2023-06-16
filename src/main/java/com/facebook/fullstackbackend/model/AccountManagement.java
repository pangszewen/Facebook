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
    ConnectionGraph<String> graph;
    UsersConnection connection = new UsersConnection();
    DatabaseSql<String> database = new DatabaseSql<>();
    PostManagement postManager = new PostManagement();
    GroupManagement groupManager = new GroupManagement();
    Chat chat = new Chat();
    Admin admin = new Admin();
    boolean isAdmin = false;
    LinkedList<String> history;

    public void registration(){
        graph = new ConnectionGraph<>();
        System.out.println("Registration Form");
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
        graph = graph.getGraph(graph);
        connection.registerGraph(graph, username);
        System.out.println("*************************");
    }

    public User login(){
        graph = new ConnectionGraph<>();
        history = new LinkedList<>();
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
        if(builder.isBanned(user))
            return null;        

        graph = graph.getGraph(graph);
        isAdmin = admin.isAdmin(user);
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
        String gender = sc.nextLine();
        while(!builder.verifyGender(gender)){
            System.out.println("What is your gender? (MALE/FEMALE)");
            gender = sc.nextLine();
        }
        builder.setGender(gender);

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
                System.out.println( "\u001B[1m" + user.getName() + "\u001B[0m");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                System.out.println("1 - Posts");
                System.out.println("2 - About");
                System.out.println("3 - Friends");
                System.out.println("4 - Groups");
                if(isAdmin)
                    System.out.println("5 - Admin authorities");
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
                                    case 1: user = postManager.createUserPost(user);
                                            break;
                                    case 2: history = postManager.displayPosts(user, user, graph, history);
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
                                    case 2 -> graph = connection.displayRequest(user, graph);
                                    case 3 -> searchFriend(user);
                                }
                                user = database.getProfile(user.getAccountID());    // Update the user object if user accept friend requests
                            }
                            break;

                    case 4: int choiceGroup = 1;
                            while(choiceGroup>0){
                                System.out.println("<" + user.getGroups().size() + " groups>");
                                System.out.println("-------------------------");
                                System.out.println("0 - Back");
                                System.out.println("1 - My groups");
                                System.out.println("2 - Group invitations");
                                System.out.println("3 - Search group");
                                System.out.println("4 - Create group");
                                System.out.println("*************************");
                                choiceGroup = sc.nextInt();
                                sc.nextLine();
                                System.out.println("*************************");
                                switch(choiceGroup){
                                    case 1 -> user = displayGroups(user);
                                    case 2 -> displayInvitation();
                                    case 3 -> searchGroups();
                                    case 4 -> user = groupManager.createGroup(user);
                                }
                            }
                            break;

                    case 5: if(isAdmin){
                                int choiceAdmin = 1;
                                while(choiceAdmin>0){
                                    System.out.println("0 - Back");
                                    System.out.println("1 - Ban user");
                                    System.out.println("2 - Delete user");
                                    System.out.println("3 - Set user as admin");
                                    System.out.println("4 - Add new prohibited words");
                                    System.out.println("*************************");
                                    choiceAdmin = sc.nextInt();
                                    sc.nextLine();
                                    System.out.println("*************************");
                                    switch(choiceAdmin){
                                        case 1,2,3 -> searchUsers();
                                        case 4 -> admin.updateProhibitedWord();
                                    }
                                }
                            }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            sc.nextLine();
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

                System.out.println("\u001B[1m" + u1.getName() + "\u001B[0m");
                if(isFriend)
                    System.out.println("\"Friend\"");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                System.out.println("1 - Posts");
                System.out.println("2 - About");
                System.out.println("3 - Friends");
                System.out.println("4 - Groups");
                System.out.println("5 - Chat");
                if(isAdmin)
                    System.out.println("6 - Admin authorities");
                System.out.println("-------------------------");
                if(isFriend)
                    System.out.println("7 - Remove friend");    // if they are already friends
                else if(statusRequest = connection.checkRequest(u1, user)){
                    System.out.println(u1.getName() + " has requested to be your friend");
                    System.out.println("7 - Confirm request");  // if the searched user have sent a friend request to the user
                    System.out.println("8 - Delete request");
                    isFriend=true;
                }else
                    statusRequest = connection.checkRequest(user, u1);

                if(u1.getUsername() != user.getUsername()){
                    if(!isFriend){
                        if(statusRequest)
                            System.out.println("7 - Cancel friend request");
                        else
                            System.out.println("7 - Add friend");
                    }
                }

                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                switch(choice){
                    case 1: history = postManager.displayPosts(user, u1, graph, history);
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

                    case 4: int choiceGroup = 1;
                            while(choiceGroup>0){
                                System.out.println("<" + u1.getGroups().size() + " groups>");
                                System.out.println("-------------------------");
                                System.out.println("0 - Back");
                                System.out.println("1 - " + u1.getName() + "'s groups");
                                System.out.println("2 - Search group");
                                System.out.println("*************************");
                                choiceGroup = sc.nextInt();
                                sc.nextLine();
                                System.out.println("*************************");
                                switch(choiceGroup){
                                    case 1 -> u1 = displayGroups(u1);
                                    case 2 -> searchGroups();
                                }
                            }
                            break;

                    case 5: if(!chat.verifyUserChatExist(user, u1))
                                chat.createUserChat(user, u1);
                            connection.startUserChatting(user, u1);
                            break;
                    
                    case 6: if(isAdmin){
                                int choiceAdmin = 1;
                                while(choiceAdmin>0){
                                    System.out.println("Admin");
                                    System.out.println("-------------------------");
                                    System.out.println("0 - Back");
                                    System.out.println("1 - Ban user");
                                    System.out.println("2 - Delete user");
                                    if(!admin.isAdmin(u1))
                                        System.out.println("3 - Set as admin");
                                    System.out.println("*************************");
                                    choiceAdmin = sc.nextInt();
                                    sc.nextLine();
                                    System.out.println("*************************");
                                    switch(choiceAdmin){
                                        case 1: admin.banUser(u1);
                                                break;
                                        case 2: graph = admin.deleteAccount(u1, graph);
                                                return;
                                        case 3: if(!admin.isAdmin(u1)){
                                                    u1 = admin.setAdmin(u1);
                                                }else{
                                                    choiceAdmin=4;
                                                }
                                                break;
                                    }
                                }
                            }

                    case 7: if(isFriend&&!statusRequest)        // user remove friend
                                connection.removeFriend(user, u1, graph);
                            else if(isFriend&&statusRequest)    // user confirm friend request
                                graph = connection.confirmRequest(user, u1, graph);
                            else if(!isFriend&&!statusRequest)  // user send friend request
                                connection.sendRequest(user, u1);
                            else                                // user cancel friend request sent to searched user
                                connection.cancelRequest(user, u1);
                            break;

                    case 8: connection.cancelRequest(u1, user); // user delete friend request sent by searched user
                            break;
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            viewOtherPage(u1);
        }
    }

    public void viewGroupPage(Group group){
        try{
            int choice = 1;
            while(choice>0){
                boolean isGroupAdmin = groupManager.isGroupAdmin(group, user);
                boolean isGroupMember = groupManager.isMember(group, user);
                System.out.println("\u001B[1m" + group.getGroupName() + "\u001B[0m");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                System.out.println("1 - Posts");
                System.out.println("2 - About");
                System.out.println("3 - Members");
                if(isGroupMember)
                    System.out.println("4 - Group chat");
                if(isGroupAdmin)
                    System.out.println("5 - Admin authorities");
                System.out.println("-------------------------");
                if(isGroupAdmin)
                    System.out.println("6 - Delete group");    // if user is already a member of the group
                else if(isGroupMember)
                    System.out.println("6 - Leave group");
                else
                    System.out.println("6 - Join group");

                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                switch(choice){
                    case 1: if(isGroupMember){
                                int choicePost = 1;
                                while(choicePost>0){
                                    System.out.println("Posts");
                                    System.out.println("-------------------------");
                                    System.out.println("0 - Back");
                                    System.out.println("1 - Create group post");
                                    System.out.println("2 - Group posts");
                                    System.out.println("*************************");
                                    choicePost = sc.nextInt();
                                    System.out.println("*************************");
                                    switch(choicePost){
                                        case 1: group = postManager.createGroupPost(group, user);
                                                break;
                                        case 2: history = postManager.displayGroupPosts(group, user, history);
                                                break;
                                    }
                                }
                            }else
                                history = postManager.displayGroupPosts(group, user, history);
                            break;

                    case 2: int choiceAbout = 1;
                            while(choiceAbout>0){
                                groupManager.viewGroupInfo(group);
                                System.out.println("0 - Back"); 	
                                if(isGroupAdmin)
                                    System.out.println("1 - Edit group info");
                                System.out.println("*************************");
                                choiceAbout = sc.nextInt();
                                sc.nextLine();
                                System.out.println("*************************");
                                if(choiceAbout==1 && isGroupAdmin)
                                    group = groupManager.editGroupInfo(group);
                            }               
                            break;

                    case 3: int choiceMembers = 1;
                            while(choiceMembers>0){
                                System.out.println("<" + group.getNoOfMembers() + " members>");
                                System.out.println("-------------------------");
                                System.out.println("0 - Back");
                                System.out.println("1 - View members");
                                if(isGroupMember)
                                    System.out.println("2 - Invite members");
                                if(isGroupAdmin)
                                    System.out.println("3 - Remove members");
                                System.out.println("*************************");
                                choiceMembers = sc.nextInt();
                                sc.nextLine();
                                System.out.println("*************************");
                                switch(choiceMembers){
                                    case 1: displayMembers(group, user);
                                            break;
                                    case 2: if(isGroupMember)
                                                groupManager.inviteMember(group, user);
                                            break;
                                    case 3: if(isGroupAdmin)
                                                groupManager.removeMember(group);
                                            break;
                                }
                            }
                            break;
                    
                    case 4: if(isGroupMember){
                                if(!chat.verifyGroupChatExist(group))
                                    chat.createGroupChat(group);
                                connection.startGroupChatting(user, group);
                            }
                            break;
                    
                    case 5: if(isGroupAdmin){
                                int choiceAdmin = 1;
                                while(choiceAdmin>0 && isGroupAdmin){
                                    System.out.println("Group Admin");
                                    System.out.println("-------------------------");
                                    System.out.println("0 - Back");
                                    System.out.println("1 - Edit group info");
                                    System.out.println("2 - Remove member");
                                    System.out.println("3 - Delete group");
                                    System.out.println("*************************");
                                    choiceAdmin = sc.nextInt();
                                    sc.nextLine();
                                    System.out.println("*************************");
                                    switch(choiceAdmin){
                                        case 1: group = groupManager.editGroupInfo(group);
                                                break;
                                        case 2: group = groupManager.removeMember(group);
                                                break;
                                        case 3: database.deleteGroup(group);
                                                choiceAdmin = 0;     // Break loop
                                                choice = 0;
                                                break;
                                    }
                                    isGroupAdmin = groupManager.isGroupAdmin(group, user);
                                }
                            }
                            break;

                    case 6: if(isGroupAdmin){
                                database.deleteGroup(group);    // If user is group admin
                                choice = 0;
                            }else if(isGroupMember)
                                group = groupManager.leaveGroup(group, user);   // If user is a group member
                            else
                                group = groupManager.joinGroup(group, user);    // If user is not a group member
                            break;
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            viewGroupPage(group);
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
                    System.out.println("-1 - Back to Friends tab");
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

                    // Update user object
                    u1 = database.getProfile(u1.getAccountID());

                    result = database.ifContains(emailOrPhoneNoOrUsernameOrName);     // ArrayList of user objects of search result
                    usernameResult.clear();

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
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
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
                    choice = 2;
                    while(choice>1){
                        System.out.println("No result found.");
                        System.out.println("-------------------------");
                        System.out.println("0 - Back");
                        System.out.println("1 - Search again");
                        System.out.println("-1 - Back to Home page");
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
                    System.out.println("-1 - Back to Home page");
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
                        if(!result.get(choice-1).getUsername().equals(user.getUsername()))
                            viewOtherPage(result.get(choice-1));
                        else
                            viewMyPage();
                    }

                    result = database.ifContains(emailOrPhoneNoOrUsernameOrName);     // ArrayList of user objects of search result

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
                            System.out.println("-1 - Back to Home page");
                            System.out.println("*************************");
                            choice = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                        }
                        if(choice == 1)
                            choice = result.size()+1;
                    }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            searchUsers();
        }
    }

    public void searchGroups(){
        try{
            int choice = 1;
            while(choice>0){
                choice = 1;     // Initialization
                System.out.println("Enter search keyword:");
                String groupIDorGroupName =  sc.nextLine();
                System.out.println("*************************");
                ArrayList<Group> result = database.ifContainsGroup(groupIDorGroupName);     // ArrayList of user objects of search result

                // Sort the names alphabetically
                for(int i=1; i<result.size(); i++){
                    for(int j=0; j<i; j++){
                        if(result.get(i).getGroupName().compareTo(result.get(j).getGroupName())<0){
                            Group temp = result.get(i);
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
                        System.out.println("-1 - Back to Groups tab");
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
                    for(Group x : result){
                        String title = "New";
                        if(x.getAdminID().equals(user.getAccountID()))
                            title = "Admin";
                        else if(groupManager.isMember(x, user))
                            title = "Member";
                        System.out.println(count + " - " + x.getGroupName() + " \"" + title + "\"");
                        count++;
                    }
            
                    System.out.println("-------------------------");
                    System.out.println(result.size()+1 + " - Search again");
                    System.out.println("-1 - Back to Groups tab");
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
                        viewGroupPage(result.get(choice-1));
                    }

                    result = database.ifContainsGroup(groupIDorGroupName);     // ArrayList of user objects of search result

                    // Sort the names alphabetically
                    for(int i=1; i<result.size(); i++){
                        for(int j=0; j<i; j++){
                            if(result.get(i).getGroupName().compareTo(result.get(j).getGroupName())<0){
                                Group temp = result.get(i);
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
                            System.out.println("-1 - Back to Groups tab");
                            System.out.println("*************************");
                            choice = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                        }
                        if(choice == 1)
                            choice = result.size()+1;
                    }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            searchGroups();
        }
    }

    // Method for displayFriends() with no arguments - used in test class
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
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
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
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayMutual(u1, u2, graph);
        }
    }

    public void displayRecommendedUsers(){
        try{
            ArrayList<User> recomUser = connection.recommendedUser(user, graph);
            if(recomUser.size()==0){
                int choice = 1;
                while(choice!=0){
                    System.out.println("No more recommendations");
                    System.out.println("-------------------------");
                    System.out.println("0 - Back");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                }
            }
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
                    else
                        System.out.println("0 - Refresh");
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
                        i=-1;
                    
                    switch(choice){
                        case 0: continue;
                        case 1: if(statusRequest)
                                    connection.cancelRequest(user, recomUser.get(i));
                                else if(pendingRequest){
                                    graph = connection.confirmRequest(recomUser.get(i), user, graph);
                                }else
                                    connection.sendRequest(user, recomUser.get(i));
                                i--;
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
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayRecommendedUsers();
        }
    }

    public User displayGroups(User u1){
        try{
            int choice = 1;
            while(choice!=0){
                ArrayList<String> groupsID = u1.getGroups();
                ArrayList<Group> groups = new ArrayList<>();
                for(String x : groupsID){
                    groups.add(database.getGroup(x));
                }
                System.out.println("<" + groupsID.size() + " groups>");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                for(int i=0; i<groups.size(); i++){
                    System.out.println((i+1) + " - " + groups.get(i).getGroupName());
                }
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                if(choice>0 && choice<=groups.size())
                    viewGroupPage(groups.get(choice-1));
                u1 = database.getProfile(u1.getAccountID());  // Update user object
            }
            return u1;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayGroups(u1);
        }
        System.out.println("Failed to display groups");
        return u1;
    }

    public void displayMembers(Group group, User user){
        try{
            ArrayList<String> membersID = group.getMembers();
            ArrayList<User> members = new ArrayList<>();
            for(int i=0; i<membersID.size(); i++){
                members.add(database.getProfile(membersID.get(i)));
            }
            int choice = 1;
            while(choice>0){
                System.out.println("<" + members.size() + " members>");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                for(int i=0; i<members.size(); i++){
                    System.out.println((i+1) + " - " + members.get(i).getName());
                }
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");

                if(choice>0){
                    User u1 = members.get(choice-1);
                    if(u1.getAccountID().equals(user.getAccountID()))
                        viewMyPage();
                    else    
                        viewOtherPage(u1);
                }
            }

        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayMembers(group, user);
        }
    }

    public void displayInvitation(){
        try{
            int choice = -1;
            while(choice!=0){
                ArrayList<String> groupInvitations = user.getGroupInvitations();
                ArrayList<String> groupsID = new ArrayList<>();
                ArrayList<Group> groups = new ArrayList<>();
                
                for(int i=0; i<groupInvitations.size(); i++){
                    String[] xArr = groupInvitations.get(i).split(":");
                    if(!groupsID.contains(xArr[1]))
                        groupsID.add(xArr[1]);

                    if(database.verifyGroupExists(xArr[1])){
                        // Check if inviter is still a member of the group
                        User inviter = database.getProfile(xArr[0]);
                        Group group = database.getGroup(xArr[1]);
                        if(!groupManager.isMember(group, inviter)){     // If not, the invitation will be removed
                            groupInvitations.remove(i);
                            user.setGroupInvitations(groupInvitations);
                            database.updateUserProfile(user, "groupInvitations", String.join(",", user.getGroupInvitations()));
                        }
                    }
                }
                for(String x : groupsID){
                    if(database.verifyGroupExists(x))
                        groups.add(database.getGroup(x));
                    else{   
                        groupManager.deleteInvitation(x, user);   // Group was deleted by admin after invitation was sent to user
                        user = database.getProfile(user.getAccountID());
                        groupInvitations = user.getGroupInvitations();  // Update the new group invitations
                    }
                }

                System.out.println("<" + groupInvitations.size() + " invitations>");
                System.out.println("-------------------------");
                System.out.println("0 - Back");
                for(int i=0; i<groups.size(); i++){
                    System.out.println((i+1) + " - " + groups.get(i).getGroupName());
                }
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                int choiceInvite = 1;
                if(choice==0)
                    choiceInvite = 0;
                while(choiceInvite==1 || choiceInvite<0 || choiceInvite>3){
                    Group group = groups.get(choice-1);
                    System.out.println(group.getGroupName());
                    System.out.println("-------------------------");
                    System.out.println("Invited by:");
                    for(String x : groupInvitations){
                        String[] xArr = x.split(":");
                        User invitor = database.getProfile(xArr[0]);
                        System.out.println(invitor.getName());
                    }
                    System.out.println("-------------------------");
                    System.out.println("0 - Back");
                    System.out.println("1 - View group");
                    System.out.println("2 - Accept invitation");
                    System.out.println("3 - Delete invitation");
                    System.out.println("*************************");
                    choiceInvite = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                    switch(choiceInvite){
                        case 1 -> viewGroupPage(group);
                        case 2 -> groupManager.confirmInvitation(group, user);
                        case 3 -> groupManager.deleteInvitation(group.getGroupID(), user);
                    }
                }
            }
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayInvitation();
        }
    }

    public void displayHistory(){
        try{
            System.out.println("\tHistory");
            System.out.println("-------------------------");
            int choice = 1;
            if(history.getSize()==0){
                while(choice!=-1){
                    System.out.println("No history yet");
                    System.out.println("-1 - Back to main page");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                }
            }else{
                history = history.iterateForward(user, history, graph);
            }  
        }catch(InputMismatchException e){
            System.out.println("*************************");
            sc.nextLine();
            displayHistory();;
        }
    }
}
