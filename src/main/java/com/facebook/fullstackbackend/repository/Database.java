package com.facebook.fullstackbackend.repository;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facebook.fullstackbackend.model.User;
import com.facebook.fullstackbackend.model.UserBuilder;

public class Database {
    Random rand = new Random();
    UserBuilder builder;
    DatabaseSql<String> mySQL = new DatabaseSql<>();
    String csvFile = "userData.csv";

    public Database(){}

    // Store new registered user into database
    public void registerUser(User user){
        /*
        try {
            //mySQL.connectAndFetchData();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/userprofile", "root", "assignment");
            PreparedStatement ps = con.prepareStatement("INSERT INTO usersdata (accountID, username, email, phoneNo, password, role) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, user.getAccountID());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNo());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole());
            ps.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
        //mySQL.registerUser(user);
        /*
        String[] user_data = {user.getAccountID(), user.getUsername(), user.getEmail(), user.getPhoneNo(), user.getPassword(), user.getRole()};
        String line = String.join(",", user_data);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
            // Write the new user data into CSV file
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
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
                if (dbPassword.equals(password)) {
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

    // Store setup profile information into database
    public void setupProfile(User user){
        //mySQL.setupProfile(user);
        /*
        String accountID = user.getAccountID();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");
                if(rowData[0].equals(accountID)){
                    lines.add(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,\"%s\",%s,%s,%s,\"%s\",\"%s\",\"%s\"", user.getAccountID(), user.getUsername(), user.getEmail(), user.getPhoneNo(), user.getPassword(), user.getRole(), user.getName(), user.getBirthday(), user.getAge(), user.getAddress(), user.getGender(), user.getStatus(), user.getNoOfFriends(), user.getHobbies(), user.getJobs(), user.getRequestList()));
                }else
                    lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            for (String rowData : lines) {
                String line = String.join(",", rowData);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    // Delete user account
    public void deleteAccount(User user){
        mySQL.deleteAccount(user);
    }

    // Get User object
    public User getProfile(String emailOrPhoneNoOrUsername){
         return mySQL.getProfile(emailOrPhoneNoOrUsername);
        /*
        UserBuilder userProfile = new UserBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split string with "," but ignoring the "," in ArrayList and Stack
                String[] rowData = line.split(",(?![^\\[]*\\])");
                if(rowData[1].equals(emailOrPhoneNoOrUsername) || rowData[2].equals(emailOrPhoneNoOrUsername) || rowData[3].equals(emailOrPhoneNoOrUsername)){
                    userProfile.setAccountID(rowData[0]);
                    userProfile.setUsername(rowData[1]);
                    userProfile.setEmail(rowData[2]);
                    userProfile.setPhoneNo(rowData[3]);
                    userProfile.setPassword(rowData[4]);
                    userProfile.setRole(rowData[5]);
                
                    if(rowData.length>6){
                        userProfile.setName(rowData[6]);
                        userProfile.setBirthday(rowData[7]);
                        userProfile.setAge(Integer.parseInt(rowData[8]));
                        userProfile.setAddress(rowData[9].substring(1, rowData[9].length()-1));
                        userProfile.setGender(rowData[10].charAt(0));
                        userProfile.setStatus(rowData[11]);
                        userProfile.setNoOfFriends(Integer.parseInt(rowData[12]));
                        // Extract information within the "[]"
                        String[] tempArray = rowData[13].substring(2, rowData[13].length()-2).split(", ");
                        ArrayList<String> tempList = new ArrayList<>();
                        for(String x : tempArray){
                            tempList.add(x);
                        }
                        userProfile.setHobbies(tempList);
                        
                        Stack<String> jobs = new Stack<>();
                        String[] tempStack = rowData[14].substring(2, rowData[14].length()-2).split(", ");
                        for(String x : tempStack){
                            jobs.push(x);
                        }
                        userProfile.setJobs(jobs);
                        
                        Stack<String> requestList = new Stack<>();
                        tempStack = rowData[15].substring(2, rowData[15].length()-2).split(", ");
                        for(String x : tempStack){
                            requestList.push(x);
                        }
                        userProfile.setRequestList(requestList);
                    }
                    return userProfile.build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("No such user found.");
        return null;
        */
    }

    // Find users with search keyword
    public ArrayList<User> ifContains(String emailOrPhoneNoOrUsernameOrName) {
        emailOrPhoneNoOrUsernameOrName = emailOrPhoneNoOrUsernameOrName.toLowerCase();
        ArrayList<User> contains = new ArrayList<>();
        ArrayList<User> tempContains = new ArrayList<>();
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM userprofile WHERE UserName LIKE ?");
            ps.setString(1, "%" + emailOrPhoneNoOrUsernameOrName + "%");
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
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            PreparedStatement ps = con.prepareStatement("SELECT requestList FROM userprofile WHERE username = ?");
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String requestListString = rs.getString("requestList");
                if (requestListString.length() > 4) {
                    String[] usernameRequestList = requestListString.split(", ");
                    if (usernameRequestList.length > 0) {
                        for (String x : usernameRequestList) {
                            User requestedUser = getProfile(x);
                            if (requestedUser != null) {
                                requestList.add(requestedUser);
                            }
                        }
                    }
                }
            }
            
            rs.close();
            con.close();
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
