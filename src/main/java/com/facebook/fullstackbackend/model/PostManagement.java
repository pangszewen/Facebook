package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class PostManagement {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
    Admin admin = new Admin();

    // Method to create new user post.
    public User createUserPost(User user){
        try{
            PostBuilder postBuilder = new PostBuilder(user);

            // Used to construct the content of the post.
            StringBuilder strBuilder = new StringBuilder();
            System.out.println("Create Post");
            System.out.println("-------------------------");
            System.out.println("\u001B[1m" + user.getName() + "\u001B[0m");
            System.out.println("What's on your mind?");
            System.out.println("(Enter \"/n\" to end your content)");
            System.out.println("-------------------------");
            String content = sc.nextLine();
            while(!content.contains("/n")){
                strBuilder.append(content);
                content = "\n" + sc.nextLine();
            }
            postBuilder.setContent(strBuilder.toString());
            System.out.println("*************************");

            int choice = 3;     // Initialization
            while(choice!=1 && choice!=2){
                System.out.println("Setting of post");
                System.out.println("-------------------------");
                System.out.println("1 - Public");
                System.out.println("2 - Private");
                System.out.println("*************************");
                choice = sc.nextInt();
                System.out.println("*************************");
            }
            switch(choice){
                case 1 -> postBuilder.setStatus(Post.Status.PUBLIC);
                case 2 -> postBuilder.setStatus(Post.Status.PRIVATE);
            }
            // Get Post object.
            Post post = postBuilder.build();

            choice = 2;     // Initialization
            while(choice!=0 && choice!=1){
                System.out.println("0 - Delete draft");
                System.out.println("1 - Post draft");
                System.out.println("*************************");
                choice = sc.nextInt();
                System.out.println("*************************");
            }
            // User post successfully created.
            if(choice==1){
                // Upload post to database
                database.uploadPost(post); 
                // Update user object
                user.setNoOfCreatedPost(user.getNoOfCreatedPost()+1);
                DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
                databaseInt.updateUserProfile(user, "noOfCreatedPost", user.getNoOfCreatedPost());
            }

            // Post will be automatically removed if contains inappropriate content.
            autoRemoveInappropriateContent(post);
            return user;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            createUserPost(user);
        }
        System.out.println("Failed to create user post");
        return user;
    }

    // Method to create new group post (not included into the noOfCreatedPost of user)
    public Group createGroupPost(Group group, User user){
        try{
            PostBuilder postBuilder = new PostBuilder(group, user);

            // Used to construct the content of the post.
            StringBuilder strBuilder = new StringBuilder();
            System.out.println("Create Group Post");
            System.out.println("-------------------------");
            System.out.println(user.getName());
            System.out.println("What's on your mind?");
            System.out.println("(Enter \"/n\" to end your content)");
            System.out.println("*************************");
            String content = sc.nextLine();
            while(!content.contains("/n")){
                strBuilder.append(content);
                content = "\n" + sc.nextLine();
            }
            postBuilder.setContent(strBuilder.toString());
            System.out.println("*************************");
            Post post = postBuilder.build();

            int choice = 2;     // Initialization
            while(choice!=0 && choice!=1){
                System.out.println("0 - Delete draft");
                System.out.println("1 - Post draft");
                System.out.println("*************************");
                choice = sc.nextInt();
                System.out.println("*************************");
            }
            // Group post successfully created.
            if(choice==1){
                // Upload post to database
                database.uploadPost(post); 
                // Update user object
                group.setNoOfCreatedPost(group.getNoOfCreatedPost()+1);
                DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
                databaseInt.updateGroup(group, "noOfCreatedPost", group.getNoOfCreatedPost());
            }

            // Post will be automatically removed if contains inappropriate content.
            autoRemoveInappropriateContent(post);
            return group;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            createGroupPost(group, user);
        }
        System.out.println("Failed to create group post");
        return group;
    }

    // Method to delete existing user post.
    public void deleteUserPost(Post post, User user){
        database.deletePost(post);
        user.setNoOfDeletedPost(user.getNoOfDeletedPost()+1);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updateUserProfile(user, "noOfDeletedPost", user.getNoOfDeletedPost());
    }

    // Method to delete existing group post.
    public void deleteGroupPost(Post post, Group group){
        database.deletePost(post);
        group.setNoOfDeletedPost(group.getNoOfDeletedPost()+1);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updateGroup(group, "noOfDeletedPost", group.getNoOfDeletedPost());
    }

    // Method to like post.
    public Post likePost(Post post, User user){
        post.setLikes(post.getLikes()+1);   // Increment of likes
        ArrayList<String> likeList = database.getPostList(post, "likeList");
        likeList.add(user.getUsername());
        database.updatePostList(post, "likeList", likeList);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updatePost(post, "likes", post.getLikes());
        return post;
    }

    // Method to unlike post.
    public Post unlikePost(Post post, User user){
        post.setLikes(post.getLikes()-1);   // Decrement of likes
        ArrayList<String> likeList = database.getPostList(post, "likeList");
        likeList.remove(user.getUsername());
        database.updatePostList(post, "likeList", likeList);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updatePost(post, "likes", post.getLikes());
        return post;
    }

    // Method to comment post.
    public Post commentPost(Post post, User user){
        try{
            int choice = 3;
            while(choice>1){
                choice = 3;     // Initialization
                // Used to construct the comment.
                StringBuilder strBuilder = new StringBuilder();
                System.out.println("Write your comment.");
                System.out.println("(Enter \"/n\" to end your comment)");
                System.out.println("-------------------------");
                String content = sc.nextLine();
                while(!content.contains("/n")){
                    strBuilder.append(content);
                    content = "\n" + sc.nextLine();
                }
                String comment = strBuilder.toString();
                System.out.println("-------------------------");

                while(choice<0 || choice>2){
                    System.out.println("0 - Back");
                    System.out.println("1 - Post comment");
                    System.out.println("2 - Delete comment");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                }
                // Comment successfully posted.
                if(choice==1){
                    post.setComments(post.getComments()+1); // Increment comments
                    String userComments = user.getUsername() + ":" + comment;
                    ArrayList<String> commentList = database.getPostList(post, "commentList");
                    commentList.add(userComments);
                    database.updatePostList(post, "commentList", commentList);
                    DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
                    databaseInt.updatePost(post, "comments", post.getComments());
                }
            }
            return post;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            commentPost(post, user);
        }
        System.out.println("Failed to comment post");
        return post;
    }

    // Method to view post.
    public void viewPost(Post post){
        User user = database.getProfile(post.getUserID());
        System.out.println("\u001B[1m" + user.getName() + "\u001B[0m");     // Bold text

        if(post.getStatus().equals(Post.Status.GROUP)){
            String[] arr = post.getPostID().split("G");
            Group group = database.getGroup(arr[0]);
            System.out.println("<" + group.getGroupName() + ">");   // Display group name
        }else
            System.out.println("<" + String.valueOf(post.getStatus()).toLowerCase() + ">");     // Display public or private

        System.out.println();
        System.out.println(post.getContent());
        System.out.println();
        System.out.println(post.getPostTime());
        System.out.println("-------------------------");
        System.out.println(post.getLikes() + " likes\t\t" + post.getComments() + " comments");  // Display number of likes and comments
        System.out.println("*************************");
    }

    // Method to access post action (like, comment, view likes and comments, etc). This method is used when viewing post from history.
    public LinkedList<String> displayPostAction(User user, Post post, LinkedList<String> history){
        try{
            int choice = 5;
            while(choice!=-1){
                ArrayList<String> likeList = database.getPostList(post, "likeList");
                String[] postID;
                Group group = null;
                if(post.getStatus().equals(Post.Status.GROUP)){
                    postID = post.getPostID().split("G");
                    group = database.getGroup(postID[0]);
                }
                User u1 = database.getProfile(post.getUserID());
                viewPost(post);
                            
                boolean likeStatus = false;
                for(String x : likeList){
                    if(x.equals(user.getUsername())){   // If user has liked the post, he is able to unlike it
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
                if(post.getPostID().contains("G")){
                    // For group post, only group admin or the creator of the post can delete it.
                    if(user.getAccountID().equals(post.getUserID()) || user.getAccountID().equals(group.getAdminID()))
                        System.out.println("5 - Delete post");
                }else{
                    // For user post, only admin of Facebook or creator of the post can delete it.
                    if(user.getAccountID().equals(post.getUserID()) || admin.isAdmin(user))
                    System.out.println("5 - Delete post");
                }
                System.out.println("-1 - Back to history page");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                if(choice>0){
                    switch(choice){
                        case 1: if(likeStatus)
                                    unlikePost(post, user);
                                else
                                    likePost(post, user);
                                break;
                        case 2: viewLikes(post);
                                break;
                        case 3: commentPost(post, user);
                                break;
                        case 4: viewComments(post);
                                break;
                        case 5: if(post.getPostID().contains("G")){
                                    if(user.getAccountID().equals(post.getUserID()) || user.getAccountID().equals(group.getAdminID())){
                                        deleteGroupPost(post, group);
                                        history = history.remove(post.getPostID(), history);    // Remove post from history too.
                                    }
                                }else{
                                    if(user.getAccountID().equals(post.getUserID()) || admin.isAdmin(user)){    // If is creator of post
                                        deleteUserPost(post, user);
                                        history = history.remove(post.getPostID(), history);
                                    }else if(admin.isAdmin(user)){  // If is admin
                                        admin.manuallyRemoveInappropriateContent(post, u1);
                                        history = history.remove(post.getPostID(), history);    
                                    }
                                }  
                                break;
                    }
                    
                    if(choice==2 || choice==4){     // If user select to view likes or comments.
                        while(choice!=0 && choice!=-1){
                            System.out.println("0 - Back");
                            System.out.println("-1 - Back to history page");
                            System.out.println("*************************");
                            choice = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                        }
                    }else if(choice==5){    // If user select to delete post.
                        if(post.getPostID().contains("G")){
                            if(user.getAccountID().equals(post.getUserID()) || user.getAccountID().equals(group.getAdminID())){
                                 System.out.println("Post successfully deleted");
                                System.out.println("*************************");
                                choice = -1;    // Break loop
                            }
                        }else{
                            if(user.getAccountID().equals(post.getUserID()) || admin.isAdmin(user)){
                                System.out.println("Post successfully deleted");
                                System.out.println("*************************");
                                choice = -1;    // Break loop
                            }
                        }
                    }
                }
            } 
            return history;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayPostAction(user, post, history);
        }
        System.out.println("Failed to display post action");
        return history;       
    }

    // Method to view likes of post.
    public void viewLikes(Post post){
        ArrayList<String> likeList = database.getPostList(post, "likeList");    // List of usernames of users who like the post.
        System.out.println("<" + post.getLikes() + " likes>");
        System.out.println("-------------------------");
        for(String x : likeList){
            System.out.println(database.getProfile(x).getName());   // Display the name of user account, not username.
        }
        if(post.getLikes()!=0)
            System.out.println("-------------------------");
    }   

    // Method to view comments of post.
    public void viewComments(Post post){
        ArrayList<String> commentList = database.getPostList(post, "commentList");
        System.out.println("<" + post.getComments() + " comments>");
        System.out.println("-------------------------");
        for(String x : commentList){
            String[] commentInfo = x.split(":");
            System.out.println(database.getProfile(commentInfo[0]).getName() + ":");
            System.out.println(commentInfo[1]);
            System.out.println("-------------------------");
        }
    }

    // Content is automatically determined whether it's appropriate or not by checking the prohibited words list.
    // This method is implemented after user posted new post, if it is inappropriate, the post will be taken down immediately.
    public void autoRemoveInappropriateContent(Post post) {        
        if(database.containsOffensiveLanguage(post.getContent())){
            if(post.getStatus().equals(Post.Status.GROUP)){
                String[] postID = post.getPostID().split("G");
                deleteGroupPost(post, database.getGroup(postID[0]));
            }else{
                String[] postID = post.getPostID().split("U");
                deleteUserPost(post, database.getProfile(postID[0]));
            }
            // Notify the user that the post is taken down because contains inappropriate content.
            System.out.println("***Your post contains inappropriate content**");
            System.out.println("***Your post has been automatically deleted by the system***");
            System.out.println("*************************"); 
        }
    }

    // Method to display all user posts by priotizing newest post first.
    public LinkedList<String> displayUserPosts(User user, User u1, ConnectionGraph<String> graph, LinkedList<String> history){
        try{
            ArrayList<Post> yourPosts = database.getUserPosts(user, u1, graph);     // Get ArrayList of arranged user posts with the newest post in front.
            System.out.println("<" + yourPosts.size() + " posts>");
            System.out.println("-------------------------");
            int choice = 1;
            if(yourPosts.size()==0){
                while(choice!=-1){
                    System.out.println("No posts yet");
                    System.out.println("-------------------------");
                    if(u1.getUsername().equals(user.getUsername())) 
                        System.out.println("-1 - Back to Posts tab");   // User viewing own page
                    else
                        System.out.println("-1 - Back to Main page");   // User viewing other user's page
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                }
            }
            
            while(choice>=0){
                for(int i=0; i<yourPosts.size(); i++){
                    yourPosts = database.getUserPosts(user, u1, graph);     // Update the ArrayList of posts (able to handle conditions if the user has deleted post in the previous loop)
                    if(yourPosts.size()==0){
                        while(choice!=-1){
                            System.out.println("No posts yet");
                            System.out.println("-------------------------");
                            if(u1.getUsername().equals(user.getUsername()))
                                System.out.println("-1 - Back to Posts tab");
                            else
                                System.out.println("-1 - Back to Main page");
                            System.out.println("*************************");
                            choice = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                        }
                        break;  // Break for loop
                    }

                    ArrayList<String> likeList = database.getPostList(yourPosts.get(i), "likeList");
                    Post post = yourPosts.get(i);
                    Group group = null;
                    String[] postID;
                    if(post.getStatus().equals(Post.Status.GROUP)){
                        postID = post.getPostID().split("G");
                        group = database.getGroup(postID[0]);
                    }else
                        postID = post.getPostID().split("U");
                    viewPost(post);
                    
                    // Keep track of user viewed post
                    if(history.contains(post.getPostID())){     // If the history already contains the viewed post, the post will be pushed to the head of history.
                        history = history.remove(post.getPostID(), history);
                        history.addFirst(post.getPostID());
                    }else   
                        history.addFirst(post.getPostID());     // If the history doesnt contains the viewed post, the post will be added as the head of history.

                    if(i<yourPosts.size()-1)
                        System.out.println("0 - Next");
                    else
                        System.out.println("0 - Refresh");  // When reached end of ArrayList of post, user are able to refresh and start over from the head of ArrayList.

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
                    if(i!=0)
                        System.out.println("5 - Back");
                    if(post.getPostID().contains("G")){
                        if(user.getAccountID().equals(post.getUserID()) || user.getAccountID().equals(group.getAdminID()))  // Creator of post or group admin
                            System.out.println("6- Delete post");
                    }else{
                        if(user.getAccountID().equals(post.getUserID()) || admin.isAdmin(user)) // Creator of post or admin of Facebook
                        System.out.println("6 - Delete post");
                    }
                    if(user.getUsername().equals(u1.getUsername()))
                        System.out.println("-1 - Back to Posts tab");   // User viewing own page
                    else    
                        System.out.println("-1 - Back to Main page");   // User viewing other user's page
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                    if(choice>0){
                        switch(choice){
                            case 1: if(likeStatus)
                                        unlikePost(post, user);
                                    else
                                        likePost(post, user);
                                    break;
                            case 2: viewLikes(post);
                                    break;
                            case 3: commentPost(post, user);
                                    break;
                            case 4: viewComments(post);
                                    break;
                            case 5: if(i!=0)
                                        i = i+2;
                                    break;
                            case 6: if(post.getPostID().contains("G")){     // Admin of Facebook is unable to remove group posts
                                        if( user.getAccountID().equals(post.getUserID()) || group.getAdminID().equals(user.getAccountID())){
                                            deleteGroupPost(post, group);
                                            history = history.remove(post.getPostID(), history);
                                        }    
                                    }else{
                                        if(user.getAccountID().equals(post.getUserID())){
                                            deleteUserPost(post, user);
                                            history = history.remove(post.getPostID(), history);
                                        }else if(admin.isAdmin(user)){
                                            admin.manuallyRemoveInappropriateContent(post, u1);
                                            history = history.remove(post.getPostID(), history);
                                        }   
                                    }
                                    break;
                        }
                        
                        if(choice==2 || choice==4){     // User select to view likes or comments.
                            System.out.println("0 - Back");
                            System.out.println("*************************");
                            choice = sc.nextInt();
                            sc.nextLine();
                            System.out.println("*************************");
                            i++;
                        }else if(choice==6){    // User select to delete post.
                            if(post.getPostID().contains("G")){
                                if(user.getAccountID().equals(post.getUserID()) || user.getAccountID().equals(group.getAdminID())){
                                    System.out.println("Post successfully deleted");
                                    System.out.println("*************************");
                                    choice = -1;    // Break loop
                                }
                            }else{
                                if(user.getAccountID().equals(post.getUserID()) || admin.isAdmin(user)){
                                    System.out.println("Post successfully deleted");
                                    System.out.println("*************************");
                                    choice = -1;    // Break loop
                                }
                            }
                        }else if(choice==0){    // User select to next or refresh.
                            continue;
                        }else{  // User select to like or comment or input out of bound.
                            i--;
                        }
                    }
                    if(choice<0)
                        break;
                }
            }
            return history;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayUserPosts(user, u1, graph, history);
        }
        System.out.println("Failed to display user posts");
        return history;
    }

    // Method to display all group posts by priotizing newest post first.
    public LinkedList<String> displayGroupPosts(Group group, User user, LinkedList<String> history){
        try{
            ArrayList<Post> groupPosts = database.getGroupPosts(group);
            System.out.println("<" + groupPosts.size() + " posts>");
            System.out.println("-------------------------");
            int choice = 1;
            if(groupPosts.size()==0){
                while(choice!=-1){
                    System.out.println("No posts yet");
                    if(group.getMembers().contains(user.getAccountID()))
                        System.out.println("-1 - Back to Posts tab");
                    else
                        System.out.println("-1 - Back to Group page");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                }
            }
            
            while(choice>=0){
                for(int i=0; i<groupPosts.size(); i++){
                    groupPosts = database.getGroupPosts(group);
                    if(groupPosts.size()==0){
                        System.out.println("No posts yet");
                        if(group.getMembers().contains(user.getAccountID()))
                            System.out.println("-1 - Back to Posts tab");
                        else
                            System.out.println("-1 - Back to Group page");
                        System.out.println("*************************");
                        choice = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                        break;
                    }
                    ArrayList<String> likeList = database.getPostList(groupPosts.get(i), "likeList");
                    Post post = groupPosts.get(i);
                    viewPost(post);
                    
                    // Keep track of user viewed post
                    if(history.contains(post.getPostID())){
                        history = history.remove(post.getPostID(), history);
                        history.addFirst(post.getPostID());
                    }else
                        history.addFirst(post.getPostID());

                    if(i<groupPosts.size()-1)
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
                    if(i!=0)
                        System.out.println("5 - Back");
                    if(user.getAccountID().equals(post.getUserID()) || group.getAdminID().equals(user.getAccountID()))
                        System.out.println("6 - Delete post");
                    if(group.getMembers().contains(user.getAccountID()))
                        System.out.println("-1 - Back to Posts tab");
                    else
                        System.out.println("-1 - Back to Group page");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                    if(choice>0){
                        switch(choice){
                            case 1: if(likeStatus)
                                        unlikePost(post, user);
                                    else
                                        likePost(post, user);
                                    break;
                            case 2: viewLikes(post);
                                    break;
                            case 3: commentPost(post, user);
                                    break;
                            case 4: viewComments(post);
                                    break;
                            case 5: if(i!=0)
                                        i = i+2;
                                    break;
                            case 6: if(user.getAccountID().equals(post.getUserID()) || user.getAccountID().equals(group.getAdminID())){
                                        deleteGroupPost(post, group);
                                        history = history.remove(post.getPostID(), history);
                                    }
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
                            if(user.getAccountID().equals(post.getUserID()) || group.getAdminID().equals(user.getAccountID())){
                                System.out.println("Post successfully deleted");
                                System.out.println("*************************");
                                choice = 0;
                            }
                        }else if(choice==0){  
                            continue;
                        }else{
                            i++;
                        }
                    }
                    if(choice<0)
                        break;
                }
            }
            return history;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            displayGroupPosts(group, user, history);
        }
        System.out.println("Failed to display group posts");
        return history;
    }
}
