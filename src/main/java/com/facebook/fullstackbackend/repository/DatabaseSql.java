package com.facebook.fullstackbackend.repository;

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

    public DatabaseSql(){}

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

    // Check if login successfully
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

    // Check whether user has setup profile 
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

    public void setupProfile(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Insert data into userprofile table
            pstmt = conn.prepareStatement("INSERT INTO userprofile (accountID, username, email, phoneNo, role, name, birthday, age, address, gender, status, noOfFriends, hobbies, jobs, requestList, noOfCreatedPost, noOfDeletedPost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
            pstmtUserAccount.executeUpdate();
            
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
        System.out.println("Failed to get user.");
        return null;
    }

    // Find users with search keyword
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
    
        for (int i = 0; i < emailOrPhoneNoOrUsernameOrName.length(); i++) {
            for (int j = 0; j < contains.size(); j++) {
                String name = contains.get(j).getName().toLowerCase();
                if (name.charAt(i) == emailOrPhoneNoOrUsernameOrName.charAt(i)) {
                    tempContains.add(contains.get(j));
                }
            }
            contains = new ArrayList<>(tempContains);
            tempContains.clear();
        }
    
        return contains;
    }

    // Get friend request list
    public ArrayList<User> getRequestList(User user) {
        ArrayList<User> requestList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from userprofile table
            pstmt = conn.prepareStatement("SELECT * FROM userprofile WHERE username = ?");
            pstmt.setString(1, user.getUsername());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String requestListString = rs.getString("requestList");
                String[] usernameRequestList = requestListString.split(",");
                if (!requestListString.equals("")) {
                    for (String x : usernameRequestList) {
                        User requestedUser = getProfile(x);
                        if (requestedUser != null) {
                            requestList.add(requestedUser);
                        }
                    }
                }
            }
            
            rs.close();
            conn.close();
            return requestList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("Failed to get friend request list.");
        return null;
    }

    // Update friend request list
    public void updateRequestList(User user, ArrayList<User> list) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String accountID = user.getAccountID();
        
        // Store the request list in terms of usernames
        ArrayList<String> usernameList = new ArrayList<>();
        for (User requestedUser : list) {
            usernameList.add(requestedUser.getUsername());
        }
        
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Update userprofile table
            pstmt = conn.prepareStatement("UPDATE userprofile SET requestList = ? WHERE accountID = ?");
            pstmt.setString(1, String.join(",", usernameList));
            pstmt.setString(2, accountID);
            pstmt.executeUpdate();
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
            pstmt.setString(3, String.valueOf(post.geStatus()));
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

    public Post getPost(String postID){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from useraccount table
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

    public ArrayList<Post> getUserPosts(User user){
        ArrayList<Post> userPosts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from useraccount table
            pstmt = conn.prepareStatement("SELECT * FROM post WHERE userID = ?");
            pstmt.setString(1, user.getAccountID());
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
                userPosts.add(postBuilder.build());
            }

            return userPosts;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Failed to get user posts list.");
        return null;
    }

    public void updatePost(Post post, String fieldName, T newValue) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Update userprofile table
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

    public ArrayList<String> getPostList(Post post, String fieldname){
        ArrayList<String> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Read from userprofile table
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

    public void updatePostList(Post post, String fieldName, ArrayList<String> list) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String postID = post.getPostID();
        
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            // Update userprofile table
            pstmt = conn.prepareStatement("UPDATE post SET " + fieldName + "=? WHERE postID=?");
            pstmt.setString(1, String.join(",", list));
            pstmt.setString(2, postID);
            pstmt.executeUpdate();
            
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public int generateAccountID() {
        int temp = rand.nextInt(100000);
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement pstmt = conn.prepareStatement("SELECT accountID FROM useraccount");
            ResultSet rs = pstmt.executeQuery();
    
            Set<Integer> accountIDs = new HashSet<>();
            while (rs.next()) {
                int storedAccountID = rs.getInt("accountID");
                accountIDs.add(storedAccountID);
            }
    
            do {
                temp = rand.nextInt(100000);
            } while (accountIDs.contains(temp));
    
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
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
