package com.facebook.fullstackbackend.repository;

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
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");

            String sql = "INSERT INTO useraccount (accountID, username, email, password, phoneNo, role) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
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
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM useraccount WHERE email = ? OR phoneNo = ?");
            ps.setString(1, emailOrPhoneNo);
            ps.setString(2, emailOrPhoneNo);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String dbPassword = rs.getString("Password");
                String hashedPassword = hashPassword(password);
                if (dbPassword.equals(hashedPassword)) {
                    rs.close();
                    con.close();
                    return true;
                }
            }
            
            rs.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("Incorrect email address/phone number or incorrect password");
        return false;
    }

    // Check whether user has setup profile 
    public boolean isSetup(String emailOrPhoneNoOrUsername){
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            pstmt = con.prepareStatement("SELECT * FROM userprofile WHERE username = ? OR email = ? OR phoneNo = ?");
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
        PreparedStatement pstmtUserProfile = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            String sql = "INSERT INTO userprofile (accountID, username, email, phoneNo, role, name, birthday, age, address, gender, status, noOfFriends, hobbies, jobs, requestList) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmtUserProfile = conn.prepareStatement(sql);
            pstmtUserProfile.setString(1, user.getAccountID());
            pstmtUserProfile.setString(2, user.getUsername());
            pstmtUserProfile.setString(3, user.getEmail());
            pstmtUserProfile.setString(4, user.getPhoneNo());
            pstmtUserProfile.setString(5, user.getRole());
            pstmtUserProfile.setString(6, user.getName());
            pstmtUserProfile.setString(7, user.getBirthday());
            pstmtUserProfile.setInt(8, user.getAge());
            pstmtUserProfile.setString(9, user.getAddress());
            pstmtUserProfile.setString(10, String.valueOf(user.getGender()));
            pstmtUserProfile.setString(11, user.getStatus());
            pstmtUserProfile.setInt(12, user.getNoOfFriends());
            pstmtUserProfile.setString(13, String.join(",", user.getHobbies()));
            pstmtUserProfile.setString(14, String.join(",", user.getJobs()));
            pstmtUserProfile.setString(15, String.join(",", user.getRequestList()));
    
            pstmtUserProfile.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
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
    
    public void deleteAccount(User user) {
        Connection conn = null;
        PreparedStatement pstmtUserAccount = null;
        PreparedStatement pstmtUserProfile = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Delete from UserAccount table
            String sqlUserAccount = "DELETE FROM useraccount WHERE accountID = ?";
            pstmtUserAccount = conn.prepareStatement(sqlUserAccount);
            pstmtUserAccount.setString(1, user.getAccountID());
            pstmtUserAccount.executeUpdate();
    
            // Delete from UserProfile table
            String sqlUserProfile = "DELETE FROM userprofile WHERE accountID = ?";
            pstmtUserProfile = conn.prepareStatement(sqlUserProfile);
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
    
            // Update UserAccount table
            String accountSql = "UPDATE useraccount SET " + fieldName + "=? WHERE accountID=?";
            pstmt = conn.prepareStatement(accountSql);
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
    
            // Update UserAccount table
            String accountSql = "UPDATE userprofile SET " + fieldName + "=? WHERE accountID=?";
            pstmt = conn.prepareStatement(accountSql);
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

    public User getAccount(String emailOrPhoneNoOrUsername) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            pstmt = con.prepareStatement("SELECT * FROM useraccount WHERE username = ? OR email = ? OR phoneNo = ?");
            pstmt.setString(1, emailOrPhoneNoOrUsername);
            pstmt.setString(2, emailOrPhoneNoOrUsername);
            pstmt.setString(3, emailOrPhoneNoOrUsername);
            ResultSet rs = pstmt.executeQuery();

            UserBuilder builder = new UserBuilder();
            if(rs.next()) {
                builder.setAccountID(rs.getString("accountID"));
                builder.setUsername(rs.getString("username"));
                builder.setEmail(rs.getString("email"));
                builder.setPhoneNo(rs.getString("phoneNo"));
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

    public User getProfile(String emailOrPhoneNoOrUsername){
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            pstmt = con.prepareStatement("SELECT * FROM userprofile WHERE username = ? OR email = ? OR phoneNo = ?");
            pstmt.setString(1, emailOrPhoneNoOrUsername);
            pstmt.setString(2, emailOrPhoneNoOrUsername);
            pstmt.setString(3, emailOrPhoneNoOrUsername);
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
                builder.setGender(rs.getString("gender").charAt(0));
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

                rs.close();
                pstmt.close();
                con.close();

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
        emailOrPhoneNoOrUsernameOrName = emailOrPhoneNoOrUsernameOrName.toLowerCase();
        ArrayList<User> contains = new ArrayList<>();
        ArrayList<User> tempContains = new ArrayList<>();
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM userprofile WHERE username = ? OR email = ? OR phoneNo = ? OR name LIKE ?");
            ps.setString(1, emailOrPhoneNoOrUsernameOrName);
            ps.setString(2, emailOrPhoneNoOrUsernameOrName);
            ps.setString(3, emailOrPhoneNoOrUsernameOrName);
            ps.setString(4, "%" + emailOrPhoneNoOrUsernameOrName + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User u = getProfile(rs.getString("username"));
                contains.add(u);
            }
    
        rs.close();
        con.close();
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
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
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
        String accountID = user.getAccountID();
        
        // Store the request list in terms of usernames
        ArrayList<String> usernameList = new ArrayList<>();
        for (User requestedUser : list) {
            usernameList.add(requestedUser.getUsername());
        }
        
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("UPDATE userprofile SET requestList = ? WHERE accountID = ?");
            ps.setString(1, String.join(",", usernameList));
            ps.setString(2, accountID);
            ps.executeUpdate();
            
            con.close();
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
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM userprofile WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String occupiedUsername = rs.getString("username");
                if(occupiedUsername.equals(username)){
                    System.out.println("This username is occupied, please use another username.");
                    return false;
                }
            }
            
            con.close();
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
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM userprofile WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String occupiedEmail = rs.getString("email");
                if(occupiedEmail.equals(email)){
                    System.out.println("This email address is occupied, please use another email address.");
                    return false;
                }
            }
            
            con.close();
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
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM userprofile WHERE phoneNo = ?");
            ps.setString(1, phoneNo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String occupiedPhoneNo = rs.getString("phoneNo");
                if(occupiedPhoneNo.equals(phoneNo)){
                    System.out.println("This phone number is occupied, please use another phone number.");
                    return false;
                }
            }
            
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public int generateAccountID() {
        int temp = rand.nextInt(100000);
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT accountID FROM useraccount");
            ResultSet rs = ps.executeQuery();
    
            Set<Integer> accountIDs = new HashSet<>();
            while (rs.next()) {
                int storedAccountID = rs.getInt("accountID");
                accountIDs.add(storedAccountID);
            }
    
            do {
                temp = rand.nextInt(100000);
            } while (accountIDs.contains(temp));
    
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
    }
}
