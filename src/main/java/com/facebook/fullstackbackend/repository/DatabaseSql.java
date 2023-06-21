package com.facebook.fullstackbackend.repository;

import com.facebook.fullstackbackend.model.ConnectionGraph;
import com.facebook.fullstackbackend.model.Group;
import com.facebook.fullstackbackend.model.GroupBuilder;
import com.facebook.fullstackbackend.model.Post;
import com.facebook.fullstackbackend.model.PostBuilder;
import com.facebook.fullstackbackend.model.User;
import com.facebook.fullstackbackend.model.UserBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class DatabaseSql<T> {
    Random rand = new Random();

    // Method to insert new user into database.
    public void registerUser(User user){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Insert data into useraccount table
            pstmt = conn.prepareStatement("INSERT INTO useraccount (accountID, username, email, password, phoneNo, role) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, user.getAccountID());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getEmail());
            String password = hashPassword(user.getPassword());
            pstmt.setString(4, password);
            pstmt.setString(5, user.getPhoneNo());
            pstmt.setString(6, user.getRole());
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Check if login successfully.
    public boolean isLogin(String emailOrPhoneNo, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from useraccount table
            pstmt = conn.prepareStatement("SELECT * FROM useraccount WHERE email = ? OR phoneNo = ?");
            pstmt.setString(1, emailOrPhoneNo);
            pstmt.setString(2, emailOrPhoneNo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String dbPassword = rs.getString("Password");
                String hashedPassword = hashPassword(password);
                if (dbPassword.equals(hashedPassword)) {
                    rs.close();
                    conn.close();
                    return true;
                }
            }
            
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("Incorrect email address/phone number or incorrect password");
        return false;
    }

    // Check whether user has setup profile.
    public boolean isSetup(String emailOrPhoneNoOrUsername){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from userprofile table
            pstmt = conn.prepareStatement("SELECT * FROM userprofile WHERE username = ? OR email = ? OR phoneNo = ?");
            pstmt.setString(1, emailOrPhoneNoOrUsername);
            pstmt.setString(2, emailOrPhoneNoOrUsername);
            pstmt.setString(3, emailOrPhoneNoOrUsername);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Method to insert user's profile details into database.
    public void setupProfile(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Insert data into userprofile table
            pstmt = conn.prepareStatement("INSERT INTO userprofile (accountID, username, email, phoneNo, role, name, birthday, age, address, gender, status, noOfFriends, hobbies, jobs, requestList, noOfCreatedPost, noOfDeletedPost, banDuration, banStartDate, groups, groupInvitations) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, user.getAccountID());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhoneNo());
            pstmt.setString(5, user.getRole());
            pstmt.setString(6, user.getName());
            pstmt.setString(7, user.getBirthday());
            pstmt.setInt(8, user.getAge());
            pstmt.setString(9, user.getAddress());
            pstmt.setString(10, String.valueOf(user.getGender()));
            pstmt.setString(11, user.getStatus());
            pstmt.setInt(12, user.getNoOfFriends());
            pstmt.setString(13, String.join(",", user.getHobbies()));
            pstmt.setString(14, String.join(",", user.getJobs()));
            pstmt.setString(15, String.join(",", user.getRequestList()));
            pstmt.setInt(16, user.getNoOfCreatedPost());
            pstmt.setInt(17, user.getNoOfDeletedPost());
            pstmt.setString(18, String.valueOf(user.getBanDuration()));
            pstmt.setString(19, user.getBanStartDate());
            if(user.getGroups().size()!=0)
                pstmt.setString(20, String.join(",", user.getGroups()));
            else
                pstmt.setString(20, "");
            if(user.getGroupInvitations().size()!=0)
                pstmt.setString(21, String.join(",", user.getGroupInvitations()));
            else
                pstmt.setString(21, "");
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Method to delete user from database.
    public void deleteAccount(User user) {
        Connection conn = null;
        PreparedStatement pstmtUserAccount = null;
        PreparedStatement pstmtUserProfile = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Delete from useraccount table
            pstmtUserAccount = conn.prepareStatement("DELETE FROM useraccount WHERE accountID = ?");
            pstmtUserAccount.setString(1, user.getAccountID());
            pstmtUserAccount.executeUpdate();
    
            // Delete from userprofile table
            pstmtUserProfile = conn.prepareStatement("DELETE FROM userprofile WHERE accountID = ?");
            pstmtUserProfile.setString(1, user.getAccountID());
            pstmtUserProfile.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmtUserAccount != null) {
                    pstmtUserAccount.close();
                }
                if (pstmtUserProfile != null) {
                    pstmtUserProfile.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Method to update user's account info. (only for changes on role and password)
    public void updateUserAccount(User user, String fieldName, String newValue) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Update useraccount table
            pstmt = conn.prepareStatement("UPDATE useraccount SET " + fieldName + "=? WHERE accountID=?");
            if(fieldName.equals("password")){
                String hashedPassword = hashPassword(newValue);
                pstmt.setString(1, hashedPassword);

            }
            pstmt.setString(1, newValue);
            pstmt.setString(2, user.getAccountID());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Method to update user's profile details.
    public void updateUserProfile(User user, String fieldName, T newValue) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Update userprofile table
            pstmt = conn.prepareStatement("UPDATE userprofile SET " + fieldName + "=? WHERE accountID=?");
            if (newValue instanceof String) 
                pstmt.setString(1, (String) newValue); // Set as String
            else if (newValue instanceof Integer) 
                pstmt.setInt(1, (Integer) newValue); // Set as Integer
            pstmt.setString(2, user.getAccountID());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get User object if user havent setup profile.
    public User getAccount(String accountIDOrEmailOrPhoneNoOrUsername) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from useraccount table
            pstmt = conn.prepareStatement("SELECT * FROM useraccount WHERE accountID = ? OR username = ? OR email = ? OR phoneNo = ?");
            pstmt.setString(1, accountIDOrEmailOrPhoneNoOrUsername);
            pstmt.setString(2, accountIDOrEmailOrPhoneNoOrUsername);
            pstmt.setString(3, accountIDOrEmailOrPhoneNoOrUsername);
            pstmt.setString(4, accountIDOrEmailOrPhoneNoOrUsername);
            ResultSet rs = pstmt.executeQuery();

            UserBuilder builder = new UserBuilder();
            if(rs.next()) {
                builder.setAccountID(rs.getString("accountID"));
                builder.setUsername(rs.getString("username"));
                builder.setEmail(rs.getString("email"));
                builder.setPhoneNo(rs.getString("phoneNo"));
                builder.setPassword(rs.getString("password"));
                builder.setRole(rs.getString("role"));
            }

            return builder.build();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Failed to get user.");
        return null;
    }

    // Method to get user object with full information.
    public User getProfile(String accountIDOrEmailOrPhoneNoOrUsername){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from userprofile table
            pstmt = conn.prepareStatement("SELECT * FROM userprofile WHERE accountID = ? OR username = ? OR email = ? OR phoneNo = ?");
            pstmt.setString(1, accountIDOrEmailOrPhoneNoOrUsername);
            pstmt.setString(2, accountIDOrEmailOrPhoneNoOrUsername);
            pstmt.setString(3, accountIDOrEmailOrPhoneNoOrUsername);
            pstmt.setString(4, accountIDOrEmailOrPhoneNoOrUsername);
            ResultSet rs = pstmt.executeQuery();
            
            UserBuilder builder = new UserBuilder();
            if(rs.next()){
                builder.setAccountID(rs.getString("accountID"));
                builder.setUsername(rs.getString("username"));
                builder.setEmail(rs.getString("email"));
                builder.setPhoneNo(rs.getString("phoneNo"));
                builder.setRole(rs.getString("role"));
                builder.setName(rs.getString("name"));
                builder.setBirthday(rs.getString("birthday"));
                builder.setAge(rs.getInt("age"));
                builder.setAddress(rs.getString("address"));
                builder.setGender(rs.getString("gender"));
                builder.setStatus(rs.getString("status"));
                builder.setNoOfFriends(rs.getInt("noOfFriends"));

                String hobbiesStr = rs.getString("hobbies");
                ArrayList<String> hobbies = new ArrayList<>();
                if (hobbiesStr != null) {
                    hobbies = new ArrayList<>(Arrays.asList(hobbiesStr.split(",")));
                }
                builder.setHobbies(hobbies);

                String jobsStr = rs.getString("jobs");
                List<String> jobsList = new ArrayList<>();
                if (jobsStr != null) {
                    jobsList = Arrays.asList(jobsStr.split(","));
                }
                Stack<String> jobsStack = new Stack<>();
                jobsStack.addAll(jobsList);
                builder.setJobs(jobsStack);

                String requestListStr = rs.getString("requestList");
                List<String> reqeustList = new ArrayList<>();
                if (requestListStr != null) {
                    reqeustList = Arrays.asList(requestListStr.split(","));
                }
                Stack<String> requestListStack = new Stack<>();
                requestListStack.addAll(reqeustList);
                builder.setRequestList(requestListStack);

                builder.setNoOfCreatedPost(rs.getInt("noOfCreatedPost"));
                builder.setNoOfDeletedPost(rs.getInt("noOfDeletedPost"));
                builder.setBanDuration(rs.getString("banDuration"));
                builder.setBanStartDate(rs.getString("banStartDate"));

                String groupsStr = rs.getString("groups");
                ArrayList<String> groups = new ArrayList<>();
                if (!groupsStr.equals("")) {
                    groups = new ArrayList<>(Arrays.asList(groupsStr.split(",")));
                }
                builder.setGroups(groups);

                String groupInvitationsStr = rs.getString("groupInvitations");
                ArrayList<String> groupInvitations = new ArrayList<>();
                if (!groupInvitationsStr.equals("")) {
                    groupInvitations = new ArrayList<>(Arrays.asList(groupInvitationsStr.split(",")));
                }
                builder.setGroupInvitations(groupInvitations);

                rs.close();
                pstmt.close();
                conn.close();

                return builder.build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Method to find users with search keyword.
    public ArrayList<User> ifContains(String emailOrPhoneNoOrUsernameOrName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        emailOrPhoneNoOrUsernameOrName = emailOrPhoneNoOrUsernameOrName.toLowerCase();
        ArrayList<User> contains = new ArrayList<>();
        ArrayList<User> tempContains = new ArrayList<>();
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from userprofile table
            pstmt = conn.prepareStatement("SELECT * FROM userprofile WHERE username = ? OR email = ? OR phoneNo = ? OR name LIKE ?");
            pstmt.setString(1, emailOrPhoneNoOrUsernameOrName);
            pstmt.setString(2, emailOrPhoneNoOrUsernameOrName);
            pstmt.setString(3, emailOrPhoneNoOrUsernameOrName);
            pstmt.setString(4, "%" + emailOrPhoneNoOrUsernameOrName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User u = getProfile(rs.getString("username"));
                contains.add(u);
            }
    
        rs.close();
        conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int count = 0;
        for(int i = 0; i<contains.size(); i++){
            String username = contains.get(i).getUsername();
            String email = contains.get(i).getEmail();
            String phoneNo = contains.get(i).getPhoneNo();
            if(username.equals(emailOrPhoneNoOrUsernameOrName) || email.equals(emailOrPhoneNoOrUsernameOrName) || phoneNo.equals(emailOrPhoneNoOrUsernameOrName)){
                User temp = contains.get(count);
                contains.set(count, contains.get(i));
                contains.set(i, temp);
                tempContains.add(contains.get(count));
                count++;
            }
        }
    
        for (int i = 0; i < emailOrPhoneNoOrUsernameOrName.length(); i++) {
            for (int j = count; j < contains.size(); j++) {
                String name = contains.get(j).getName().toLowerCase();
                if (name.charAt(i) == emailOrPhoneNoOrUsernameOrName.charAt(i)) {
                    tempContains.add(contains.get(j));
                    contains.remove(j);
                }
            }
        }
    
        return tempContains;
    }

    // Method to find groups with search keyword.
    public ArrayList<Group> ifContainsGroup(String groupIDorGroupName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        groupIDorGroupName = groupIDorGroupName.toLowerCase();
        ArrayList<Group> contains = new ArrayList<>();
        ArrayList<Group> tempContains = new ArrayList<>();
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Read from groups table
            pstmt = conn.prepareStatement("SELECT * FROM groups WHERE groupID = ? OR groupName LIKE ?");
            pstmt.setString(1, groupIDorGroupName);
            pstmt.setString(2, "%" + groupIDorGroupName + "%");
            ResultSet rs = pstmt.executeQuery();
                    
            while (rs.next()) {
                Group group = getGroup(rs.getString("groupID"));
                contains.add(group);
            }
            
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    

        for (int i = 0; i < groupIDorGroupName.length(); i++) {
            for (int j = 0; j < contains.size(); j++) {
                String name = contains.get(j).getGroupName().toLowerCase();
                if (name.charAt(i) == groupIDorGroupName.charAt(i)) {
                    tempContains.add(contains.get(j));
                }
            }
            contains = new ArrayList<>(tempContains);
            tempContains.clear();
        }
    
        return contains;
    }

    // Method to insert new post into database.
    public void uploadPost(Post post){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Insert data into post table
            pstmt = conn.prepareStatement("INSERT INTO post (postID, userID, status, content, likes, comments, likeList, commentList) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, post.getPostID());
            pstmt.setString(2, post.getUserID());
            pstmt.setString(3, String.valueOf(post.getStatus()));
            pstmt.setString(4, post.getContent());
            pstmt.setInt(5, post.getLikes());
            pstmt.setInt(6, post.getComments());
            pstmt.setString(7, "");
            pstmt.setString(8, "");
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to delete post from database.
    public void deletePost(Post post){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Delete from post table
            pstmt = conn.prepareStatement("DELETE FROM post WHERE postID = ?");
            pstmt.setString(1, post.getPostID());
            pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get Post object.
    public Post getPost(String postID){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from post table
            pstmt = conn.prepareStatement("SELECT * FROM post WHERE postID = ?");
            pstmt.setString(1, postID);
            ResultSet rs = pstmt.executeQuery();

            PostBuilder postBuilder = new PostBuilder();
            if(rs.next()) {
                postBuilder.setPostID(rs.getString("postID"));
                postBuilder.setUserID(rs.getString("userID"));
                postBuilder.setStatus(rs.getString("status"));
                postBuilder.setContent(rs.getString("content"));
                postBuilder.setLikes(rs.getInt("likes"));
                postBuilder.setComments(rs.getInt("comments"));
                postBuilder.setPostTime(rs.getString("postTime"));
            }

            return postBuilder.build();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Failed to get post.");
        return null;
    }

    // Method to get all posts of user.
    public ArrayList<Post> getUserPosts(User user, User u1, ConnectionGraph<String> graph){
        ArrayList<Post> userPosts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from post table
            pstmt = conn.prepareStatement("SELECT * FROM post WHERE userID = ?");
            pstmt.setString(1, u1.getAccountID());
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                PostBuilder postBuilder = new PostBuilder();
                postBuilder.setPostID(rs.getString("postID"));
                postBuilder.setUserID(rs.getString("userID"));
                postBuilder.setStatus(rs.getString("status"));
                postBuilder.setContent(rs.getString("content"));
                postBuilder.setLikes(rs.getInt("likes"));
                postBuilder.setComments(rs.getInt("comments"));
                postBuilder.setPostTime(rs.getString("postTime"));
                Post post = postBuilder.build();
                if(rs.getString("status").equals("PRIVATE")){
                    // Only yourself and your friends can view your private posts, admin cannot access user's private posts
                    if(privacy(user, u1, graph) || user.getUsername().equals(u1.getUsername()))
                        userPosts.add(post);
                }else
                    userPosts.add(post);     // Public posts
            }
            Collections.reverse(userPosts);     // Arrange newest post to be first
            return userPosts;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Failed to get user posts list.");
        return null;
    }

    // Method to get all posts of group.
    public ArrayList<Post> getGroupPosts(Group group){
        ArrayList<Post> groupPosts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from post table
            for(int i=0; i<group.getNoOfCreatedPost(); i++){
                pstmt = conn.prepareStatement("SELECT * FROM post WHERE postID = ?");
                pstmt.setString(1, group.getGroupID()+"G"+i);
                ResultSet rs = pstmt.executeQuery();

                if(rs.next()) {
                    PostBuilder postBuilder = new PostBuilder();
                    postBuilder.setPostID(rs.getString("postID"));
                    postBuilder.setUserID(rs.getString("userID"));
                    postBuilder.setStatus(rs.getString("status"));
                    postBuilder.setContent(rs.getString("content"));
                    postBuilder.setLikes(rs.getInt("likes"));
                    postBuilder.setComments(rs.getInt("comments"));
                    postBuilder.setPostTime(rs.getString("postTime"));
                    Post post = postBuilder.build();
                    groupPosts.add(post);
                }
            }
            Collections.reverse(groupPosts);    // Arrange newest post to be first
            return groupPosts;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Failed to get group posts list.");
        return null;
    }

    // Method to update post details into database.
    public void updatePost(Post post, String fieldName, T newValue) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Update post table
            pstmt = conn.prepareStatement("UPDATE post SET " + fieldName + "=? WHERE postID=?");
            if (newValue instanceof String) 
                pstmt.setString(1, (String) newValue); // Set as String
            else if (newValue instanceof Integer) 
                pstmt.setInt(1, (Integer) newValue); // Set as Integer
            pstmt.setString(2, post.getPostID());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get post's list of likes and comments.
    public ArrayList<String> getPostList(Post post, String fieldname){
        ArrayList<String> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from post table
            pstmt = conn.prepareStatement("SELECT * FROM post WHERE postID = ?");
            pstmt.setString(1, post.getPostID());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String listString = rs.getString(fieldname);
                String[] usernameList = listString.split(",");
                if (!listString.equals("")) {
                    for (String x : usernameList) {
                        list.add(x);
                    }
                }
            }
            
            rs.close();
            conn.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("Failed to get " + fieldname + ".");
        return null;
    }

    // Method to update post's list of likes and comments.
    public void updatePostList(Post post, String fieldName, ArrayList<String> list) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String postID = post.getPostID();
        
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Update post table
            pstmt = conn.prepareStatement("UPDATE post SET " + fieldName + "=? WHERE postID=?");
            pstmt.setString(1, String.join(",", list));
            pstmt.setString(2, postID);
            pstmt.executeUpdate();
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert new group into database.
    public void createGroup(Group group){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Insert data into groups table
            pstmt = conn.prepareStatement("INSERT INTO groups (groupID, groupName, adminID, members, noOfMembers, noOfCreatedPost, noOfDeletedPost, dateOfCreation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, group.getGroupID());
            pstmt.setString(2, group.getGroupName());
            pstmt.setString(3, group.getAdminID());
            pstmt.setString(4, String.join(",", group.getMembers()));
            pstmt.setInt(5, group.getNoOfMembers());
            pstmt.setInt(6, group.getNoOfCreatedPost());
            pstmt.setInt(7, group.getNoOfDeletedPost());
            pstmt.setString(8, group.getDateOfCreation());
            pstmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to update group details into database.
    public void updateGroup(Group group, String fieldName, T newValue){
        Connection conn = null;
        PreparedStatement pstmt = null;
        String groupID = group.getGroupID();
        
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Update groups table
            pstmt = conn.prepareStatement("UPDATE groups SET " + fieldName + "=? WHERE groupID=?");
            if (newValue instanceof String) 
                pstmt.setString(1, (String) newValue); // Set as String
            else if (newValue instanceof Integer) 
                pstmt.setInt(1, (Integer) newValue); // Set as Integer
            pstmt.setString(2, groupID);
            pstmt.executeUpdate();
            
            conn.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete group from database.
    public void deleteGroup(Group group){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Delete group from user's group list and update userproflie table
            ArrayList<String> members = group.getMembers();
            for(int i=0; i<members.size(); i++){
                User member = getProfile(members.get(i));
                ArrayList<String> groups = member.getGroups();
                groups.remove(group.getGroupID());
                member.setGroups(groups);
                pstmt = conn.prepareStatement("UPDATE userprofile SET groups = ? WHERE accountID = ?");
                pstmt.setString(1, String.join(",", member.getGroups()));
                pstmt.setString(2, member.getAccountID());
                pstmt.executeUpdate();
            }
            pstmt.close();

            // Delete from groups table
            pstmt = conn.prepareStatement("DELETE FROM groups WHERE groupID = ?");
            pstmt.setString(1, group.getGroupID());
            pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get Group object from database.
    public Group getGroup(String groupID){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from groups table
            pstmt = conn.prepareStatement("SELECT * FROM groups WHERE groupID = ?");
            pstmt.setString(1, groupID);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                GroupBuilder groupBuilder = new GroupBuilder();
                groupBuilder.setGroupID(rs.getString("groupID"));
                groupBuilder.setGroupName(rs.getString("groupName"));
                groupBuilder.setAdminID(rs.getString("adminID"));

                String membersStr = rs.getString("members");
                ArrayList<String> members = new ArrayList<>();
                if (membersStr != null) {
                    members = new ArrayList<>(Arrays.asList(membersStr.split(",")));
                }
                groupBuilder.setMembers(members);

                groupBuilder.setNoOfMembers(rs.getInt("noOfMembers"));
                groupBuilder.setNoOfCreatedPost(rs.getInt("noOfCreatedPost"));
                groupBuilder.setNoOfDeletedPost(rs.getInt("noOfDeletedPost"));
                groupBuilder.setDateOfCreation(rs.getString("dateOfCreation"));

                return groupBuilder.build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Failed to get group.");
        return null;
    }

    // Method to get all the groups joined by user.
    public ArrayList<Group> getUserGroup(User u1){
        ArrayList<Group> userGroups = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from userprofile table
            pstmt = conn.prepareStatement("SELECT * FROM userprofile WHERE accountID = ?");
            pstmt.setString(1, u1.getAccountID());
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                String groupsStr = rs.getString("groups");
                String[] groups = groupsStr.split(",");
                for(String x : groups)
                    userGroups.add(getGroup(x));
            }

            return userGroups;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Failed to get user's groups.");
        return null;
    }

    // Method to encrypt password before storing into database.
    public String hashPassword(String password) {
        try {
            //Create MessageDigest instance for SHA-256 hashing
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hashed password bytes
            byte[] hashedBytes = md.digest();
            //Convert the hashed bytes to a hexadecimal representation
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            //Return the hashed password as a string
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Validation of username
    public boolean verifyUsername(String username){
        // Check length of username
        if(username.length()<5 || username.length()>20){
            System.out.println("Your username must be between 5-20 characters.");
            return false;
        }
        
        // Username regex pattern
        String usernameRegex = "^[a-zA-Z0-9.!#$%&’*+=?^_~]*$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(usernameRegex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(username);

        // Perform the matching
        if(!matcher.matches()){
            System.out.println("Invalid username.");
            return false;
        }

        // Check for duplicated usernames
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM userprofile WHERE username = ?");
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String occupiedUsername = rs.getString("username");
                if(occupiedUsername.equals(username)){
                    System.out.println("This username is occupied, please use another username.");
                    return false;
                }
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return true;
    }

    // Validation of email
    public boolean verifyEmail(String email){
        // Email regex pattern
        String emailRegex = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}\\-~]+@[a-zA-Z0-9\\-]+(?:\\.[a-zA-Z0-9\\-]+)*$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(emailRegex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(email);

        // Perform the matching
        if(!matcher.matches()){
            System.out.println("Invalid email address.");
            return false;
        }

        // Check for duplicated emails
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM userprofile WHERE email = ?");
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String occupiedEmail = rs.getString("email");
                if(occupiedEmail.equals(email)){
                    System.out.println("This email address is occupied, please use another email address.");
                    return false;
                }
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    // Validation of phone number
    public boolean verifyPhoneNo(String phoneNo){
        // Check length of phone number
        if(phoneNo.length()<7 || phoneNo.length()>14){
            System.out.println("Your phone number must be between 7-14 digits.");
            return false;
        }
        
        // Email regex pattern
        String emailRegex = "^[0-9]+-[0-9]*$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(emailRegex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(phoneNo);

        // Perform the matching
        if(!matcher.matches()){
            System.out.println("Invalid phone number.");
            return false;
        }

        // Check for duplicated phone numbers
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM userprofile WHERE phoneNo = ?");
            pstmt.setString(1, phoneNo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String occupiedPhoneNo = rs.getString("phoneNo");
                if(occupiedPhoneNo.equals(phoneNo)){
                    System.out.println("This phone number is occupied, please use another phone number.");
                    return false;
                }
            }
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    // Method to create non duplicated ID.
    public String generateID(String tableName, String fieldName) {
        int temp = rand.nextInt(100000);
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement pstmt = conn.prepareStatement("SELECT " + fieldName + " FROM " + tableName);
            ResultSet rs = pstmt.executeQuery();
    
            Set<Integer> accountIDs = new HashSet<>();
            while (rs.next()) {
                int storedAccountID = rs.getInt(fieldName);
                accountIDs.add(storedAccountID);
            }
    
            do {
                temp = rand.nextInt(100000);
            } while (accountIDs.contains(temp));
    
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return String.valueOf(temp);
    }

    public boolean verifyBanStatus(User user){
        if(user.getBanDuration().equals("P0Y0M0D"))
            return false;
        else 
            return true;
    }

    // Method to verify if user can view the post or not.
    public boolean privacy(User user, User u1, ConnectionGraph<String> graph){
        if(user.getUsername().equals(u1.getUsername()))
            return true;
        if(graph.hasEdge(graph, user.getUsername(), u1.getUsername()))
            return true;
        else 
            return false;
    }

    // Method to verify if post exist in database.
    public boolean verifyPostExist(Post post){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from post table
            pstmt = conn.prepareStatement("SELECT * FROM post WHERE postID = ?");
            pstmt.setString(1, post.getPostID());
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Method to verify if group exist in database.
    public boolean verifyGroupExists(String groupID){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from groups table
            pstmt = conn.prepareStatement("SELECT * FROM groups WHERE groupID = ?");
            pstmt.setString(1, groupID);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Method to check whether if content contains prohibited words.
    public boolean containsOffensiveLanguage(String content) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            pstmt = conn.prepareStatement("SELECT * FROM prohibitedwords");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String word = rs.getString("words");
                if (content.toLowerCase().contains(word.toLowerCase())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Method to get list of prohibited words.
    public ArrayList<String> getProhibitedWords(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        ArrayList<String> prohibitedWords = new ArrayList<>();
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            pstmt = conn.prepareStatement("SELECT * FROM prohibitedwords");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                prohibitedWords.add(rs.getString("words"));
            }

            return prohibitedWords;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Failed to get prohibited words");
        return prohibitedWords;
    }

    // Method to insert new prohibited words into database.
    public void addNewProhibitedWord(String newProhibitedWord) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            pstmt = conn.prepareStatement("INSERT INTO prohibitedwords(words) VALUES (?)");
            pstmt.setString(1, newProhibitedWord);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to verify if user exist in database.
    public boolean verifyUserExist(String accountID){
        User user = null;
        user = getProfile(accountID);
        if(user==null)
            return false;
        else    
            return true;
    }

    // Method to check if the passwords match.
    public boolean confirmPassword(String p1, String p2){
        if(p1.equals(p2))
            return true;
        else{
            System.out.println("Password is not matched. Please try again.");
            return false;
        }
    }
}
