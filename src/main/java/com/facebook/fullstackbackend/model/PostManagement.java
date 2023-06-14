package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class PostManagement {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
    Admin admin = new Admin();

    // Create new post
    public void createPost(User user){
        PostBuilder postBuilder = new PostBuilder(user);
        StringBuilder strBuilder = new StringBuilder();
        System.out.println("Create Post");
        System.out.println("-------------------------");
        System.out.println(user.getName());
        System.out.println("What's on your mind?");
        System.out.println("(Enter \"/end\" to end your content)");
        System.out.println("*************************");
        String content = sc.nextLine();
        while(!content.contains("/end")){
            strBuilder.append(content);
            content = "\n" + sc.nextLine();
        }
        postBuilder.setContent(strBuilder.toString());
        System.out.println("*************************");
        System.out.println("Setting of post");
        System.out.println("-------------------------");
        System.out.println("1 - Public");
        System.out.println("2 - Private");
        System.out.println("*************************");
        int choice = sc.nextInt();
        System.out.println("*************************");
        switch(choice){
            case 1 -> postBuilder.setStatus(Post.Status.PUBLIC);
            case 2 -> postBuilder.setStatus(Post.Status.PRIVATE);
        }
        Post post = postBuilder.build();
        System.out.println("0 - Delete draft");
        System.out.println("1 - Post draft");
        System.out.println("*************************");
        choice = sc.nextInt();
        System.out.println("*************************");
        if(choice==1){
            database.uploadPost(post); 
            user.setNoOfCreatedPost(user.getNoOfCreatedPost()+1);
            DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
            databaseInt.updateUserProfile(user, "noOfCreatedPost", user.getNoOfCreatedPost());
        }

        autoRemoveInappropriateContent(user, post);
    }

    // Delete existing post
    public void deletePost(Post post, User user){
        database.deletePost(post);
        user.setNoOfDeletedPost(user.getNoOfDeletedPost()+1);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updateUserProfile(user, "noOfDeletedPost", user.getNoOfDeletedPost());
    }

    public Post likePost(Post post, User user){
        post.setLikes(post.getLikes()+1);
        ArrayList<String> likeList = database.getPostList(post, "likeList");
        likeList.add(user.getUsername());
        database.updatePostList(post, "likeList", likeList);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updatePost(post, "likes", post.getLikes());
        return post;
    }
    public Post unlikePost(Post post, User user){
        post.setLikes(post.getLikes()-1);
        ArrayList<String> likeList = database.getPostList(post, "likeList");
        likeList.remove(user.getUsername());
        database.updatePostList(post, "likeList", likeList);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updatePost(post, "likes", post.getLikes());
        return post;
    }

    public Post commentPost(Post post, User user){
        int choice = 0;
        while(choice==0){
            StringBuilder strBuilder = new StringBuilder();
            System.out.println("Write your comment.");
            System.out.println("(Enter \"/end\" to end your comment)");
            System.out.println("-------------------------");
            String content = sc.nextLine();
            while(!content.contains("/end")){
                strBuilder.append(content);
                content = "\n" + sc.nextLine();
            }
            String comment = strBuilder.toString();
            System.out.println("-------------------------");
            System.out.println("0 - Back");
            System.out.println("1 - Post comment");
            System.out.println("2 - Delete comment");
            System.out.println("*************************");
            choice = sc.nextInt();
            sc.nextLine();
            System.out.println("*************************");
            if(choice==1){
                post.setComments(post.getComments()+1);
                String userComments = user.getUsername() + ":" + comment;
                ArrayList<String> commentList = database.getPostList(post, "commentList");
                commentList.add(userComments);
                database.updatePostList(post, "commentList", commentList);
                DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
                databaseInt.updatePost(post, "comments", post.getComments());
            }
        }
        return post;
    }

    public void viewPost(Post post){
        User user = database.getProfile(post.getUserID());
        System.out.println("\u001B[1m" + user.getName() + "\u001B[0m");     // Bold text
        System.out.println("<" + String.valueOf(post.getStatus()).toLowerCase() + ">");
        System.out.println();
        System.out.println(post.getContent());
        System.out.println();
        System.out.println(post.getPostTime());
        System.out.println("-------------------------");
        System.out.println(post.getLikes() + " likes\t\t" + post.getComments() + " comments");
        System.out.println("*************************");
    }

    public LinkedList<String> displayPostAction(User user, Post post, LinkedList<String> history){
        int choice = 5;
        while(choice!=-1){
            ArrayList<String> likeList = database.getPostList(post, "likeList");
            User u1 = database.getProfile(post.getUserID());
            viewPost(post);
                        
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
            if(user.getAccountID().equals(post.getUserID()) || admin.isAdmin(user))
                System.out.println("5 - Delete post");
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
                    case 5: if(user.getAccountID().equals(post.getUserID())){
                                deletePost(post, user);
                                history = history.remove(post.getPostID(), history);
                            }else if(admin.isAdmin(user)){
                                admin.manuallyRemoveInappropriateContent(post, u1);
                                history = history.remove(post.getPostID(), history);
                            }   
                            break;
                }
                if(choice==2 || choice==4){
                    System.out.println("0 - Back");
                    System.out.println("-1 - Back to history page");
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    sc.nextLine();
                    System.out.println("*************************");
                }else if(choice==5){
                    if(user.getAccountID().equals(post.getUserID()) || admin.isAdmin(user)){
                        System.out.println("Post successfully deleted");
                        System.out.println("*************************");
                        choice = -1;    // Break loop
                    }
                }
            }
        } 
        return history;       
    }

    public void viewLikes(Post post){
        ArrayList<String> likeList = database.getPostList(post, "likeList");    // List of usernames of users who like the post
        System.out.println("<" + post.getLikes() + " likes>");
        System.out.println("-------------------------");
        for(String x : likeList){
            System.out.println(database.getProfile(x).getName());   // Display the name of user account, not username
        }
        if(post.getLikes()!=0)
            System.out.println("-------------------------");
    }   

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

    // Content is automatically determined whether it's appropriate or not by checking the prohibited words list
    // This method is implemented after user posted new post, if it is inappropriate, the post will be taken down immediately
    public void autoRemoveInappropriateContent(User user, Post post) {        
        if(database.containsOffensiveLanguage(post.getContent())){
            deletePost(post, user);
            System.out.println("***Your post contains inappropriate content**");
            System.out.println("***Your post has been automatically deleted by the system***");
            System.out.println("*************************"); 
        }
    }
}
