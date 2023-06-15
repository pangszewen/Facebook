package com.facebook.fullstackbackend.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class UserBuilder {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
    DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
    public String accountID;
    public String username;
    public String email;
    public String phoneNo;
    public String password;
    public String role;
    public String name;
    public String birthday;
    public int age;
    public String address;
    public User.Gender gender;
    public String status;
    public int noOfFriends;
    public ArrayList<String> hobbies = new ArrayList<>();
    public Stack<String> jobs = new Stack<>();
    public Stack<String> requestList = new Stack<>();
    public int noOfCreatedPost;
    public int noOfDeletedPost;
    public String banDuration;
    public String banStartDate;
    public ArrayList<String> groups = new ArrayList<>();
    public ArrayList<String> groupInvitations = new ArrayList<>();
    
    public UserBuilder(){
        this.accountID = null;
        this.username = null;
        this.email = null;
        this.phoneNo = null;
        this.password = null;
        this.role = "normal";
        this.noOfCreatedPost = 0;
        this.noOfDeletedPost = 0;
        this.banDuration = "P0Y0M0D";
        this.banStartDate = "";
    }

    public UserBuilder(String accountID, String username, String email, String phoneNo, String password, String role){
        this.accountID = accountID;
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.role = "normal";
        this.noOfCreatedPost = 0;
        this.noOfDeletedPost = 0;
        this.banDuration = "P0Y0M0D";
        this.banStartDate = "";
    }

    public void setAccountID(){
        this.accountID = database.generateID("useraccount", "accountID");
    }
    public void setAccountID(String accountID){
        this.accountID = accountID;
    }
    public String getAccountID(){
        return accountID;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }

    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }

    public void setPhoneNo(String phoneNo){
        this.phoneNo = phoneNo;
    }
    public String getPhoneNo(){
        return phoneNo;
    }

    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }

    public void setRole(String role){
        this.role = role;
    }
    public String getRole(){
        return role;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setBirthday(String birthday){
        this.birthday = birthday;
    }
    public String getBirthday(){
        return birthday;
    }

    public void setAge(int age){
        this.age = age;
    }
    public int getAge(){
        return age;
    }

    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return address;
    }

    public void setGender(User.Gender gender){
        this.gender = gender;
    }
    public void setGender(String gender){
        this.gender = User.Gender.valueOf(gender.toUpperCase());
    }
    public User.Gender getGender(){
        return gender;
    }

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return status;
    }

    public void setNoOfFriends(int noOfFriends){
        this.noOfFriends = noOfFriends;
    }
    public int getNoOfFriends(){
        return noOfFriends;
    }

    // For edit user profile
    public void setHobbies(String hobbies){
        this.hobbies.add(hobbies);
    }
    // For create user object
    public void setHobbies(ArrayList<String> hobbies){
        this.hobbies = hobbies;
    }
    public ArrayList<String> getHobbies(){
        return hobbies;
    }

    // For edit user profile
    public void setJobs(String job){
        this.jobs.push(job);
    }
    // For create user object
    public void setJobs(Stack<String> jobs){
        this.jobs = jobs;
    }
    public Stack<String> getJobs(){
        return jobs;
    }

    public void setRequestList(String username){
        this.requestList.push(username);
    }
    public void setRequestList(Stack<String> requestList){
        this.requestList = requestList;
    }
    public Stack<String> getRequestList(){
        return requestList;
    }

    public void setNoOfCreatedPost(int noOfCreatedPost){
        this.noOfCreatedPost = noOfCreatedPost;
    }
    public int getNoOfCreatedPost(){
        return noOfCreatedPost;
    }

    public void setNoOfDeletedPost(int noOfDeletedPost){
        this.noOfDeletedPost = noOfDeletedPost;
    }
    public int getNoOfDeletedPost(){
        return noOfDeletedPost;
    }

    public void setBanDuration(String banDuration){
        this.banDuration = banDuration;
    }
    public String getBanDuration(){
        return banDuration;
    }

    public void setBanStartDate(String banStartDate){
        this.banStartDate = banStartDate;
    }
    public String getBanStartDate(){
        return banStartDate;
    }

    public void setGroups(ArrayList<String> groups){
        this.groups = groups;
    }
    public void setGroups(String group){
        this.groups.add(group);
    }
    public ArrayList<String> getGroups(){
        return groups;
    }

    public void setGroupInvitations(ArrayList<String> groupInvitations){
        this.groupInvitations = groupInvitations;
    }
    public void setGroupInvitations(User inviter, Group group){
        String str = inviter.getAccountID() + ":" + group.getGroupID();
        this.groupInvitations.add(str);
    }
    public ArrayList<String> getGroupInvitations(){
        return groupInvitations;
    }

    // to create User object
    public User build(){
        return new User(this);
    }

    // Edit account details
    public User editPassword(User user){
        user = database.getAccount(user.getUsername());
        System.out.print("Enter your old password: ");
        String oldPassword = sc.nextLine();

        while(!database.hashPassword(oldPassword).equals(user.getPassword())){
            System.out.println("Wrong password. Please enter again.");
            //System.out.println("Enter 0 to exit.");
            System.out.print("Old password: ");
            oldPassword = sc.nextLine();
            //if(oldPassword.equals("0"))
            //    return user;
        }

        System.out.print("Enter your new password: ");
        String newPassword = sc.nextLine();
        while(!verifyPassword(newPassword)){   // Check strength of password
            System.out.print("New password: ");
            newPassword = sc.nextLine();
        }
        
        // Hashing the new password
        newPassword = database.hashPassword(newPassword);

        // Update user object
        user.setPassword(newPassword);

        // Update to database
        database.updateUserAccount(user, "password", newPassword);

        // Get back the user object
        user = database.getProfile(user.getUsername());
        return user;
    }

    public User editName(User user){
        System.out.println("Current name: " + user.getName());
        System.out.print("Change name to: ");
        String name = sc.nextLine();
        
        // Update user object
        user.setName(name);

        // Update to database
        database.updateUserProfile(user, "name", name);

        return user;
    }

    public User editBirthday(User user){
        System.out.println("Current birthday: " + user.getBirthday());
        System.out.print("Change birthday to: ");
        String birthday = sc.nextLine();
        while(!validateBirthdayFormat(birthday)){
            System.out.print("Change birthday to (format: YYYY-MM-DD): ");
            birthday = sc.nextLine();
        }
        // Update user object
        user.setBirthday(birthday);

        // Update age based on birthday
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(LocalDate.parse(birthday), currentDate);
        int age = period.getYears();
        user.setAge(age);

        // Update to database
        database.updateUserProfile(user, "birthday", birthday);
        databaseInt.updateUserProfile(user, "age", age);

        return user;
    }

    public User editAddress(User user){
        System.out.println("Current address: " + user.getAddress());
        System.out.print("Change address to: ");
        String address = sc.nextLine();
        
        // Update user object
        user.setAddress(address);

        // Update to database
        database.updateUserProfile(user, "address", address);

        return user;
    }

    public User editGender(User user){
        System.out.println("Current gender: " + user.getGender());
        System.out.print("Change gender to (MALE/FEMALE): ");
        String gender = sc.nextLine();

        // Update user object
        user.setGender(gender);

        // Update to database
        database.updateUserProfile(user, "gender", gender);

        return user;
    }

    public User editStatus(User user){
        if(user.getStatus().length()>0){
            System.out.println("Current relationship status: " + user.getStatus());
            System.out.print("Change status to: ");
        }else
            System.out.print("Relationship status: ");
        String status = sc.nextLine();

        // Update user object
        user.setStatus(status);

        // Update to database
        database.updateUserProfile(user, "status", status);

        return user;
    }

    public User editHobbies(User user){
        System.out.println("Current hobby: " + user.getHobbies().get(0));
        System.out.println("1 - Add hobby");
        System.out.println("2 - Change hobby");
        System.out.println("3 - Delete hobby");
        System.out.println("*************************");
        int choice = sc.nextInt();
        sc.nextLine();
        System.out.println("*************************");
        switch(choice){
            case 1: System.out.print("New hobby: ");
                    String hobby = sc.nextLine();
                    System.out.println("*************************");
                    // Update user object
                    user.getHobbies().add(hobby);
                    break;

            case 2: System.out.println("Select hobby: ");
                    for(int i=0; i<user.getHobbies().size(); i++){
                        System.out.println((i+1) + " - " + user.getHobbies().get(i));
                    }
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    System.out.println("*************************");
                    // Update user object
                    // Change the desired hobby to be in index 0
                    String temp = user.getHobbies().get(choice-1);
                    user.getHobbies().set(choice-1, user.getHobbies().get(0));
                    user.getHobbies().set(0, temp);
                    break;

            case 3: System.out.println("Delete hobby: ");
                    for(int i=0; i<user.getHobbies().size(); i++){
                        System.out.println((i+1) + " - " + user.getHobbies().get(i));
                    }
                    System.out.println("*************************");
                    choice = sc.nextInt();
                    System.out.println("*************************");
                    // Update user object
                    user.getHobbies().remove(choice-1);
                    break;
        }
        // Update to database
        String hobbies = String.join(",", user.getHobbies());
        database.updateUserProfile(user, "hobbies", hobbies);

        return user;
    }

    public User editJobs(User user){
        Stack<String> jobStack = user.getJobs();
        if(!jobStack.empty()){
            String currentJob = jobStack.pop();
            System.out.println("Current job: " + currentJob);
            if(!jobStack.empty())
                System.out.println("Previous job: " + jobStack.peek());
            jobStack.push(currentJob);
        }
        System.out.println("1 - Add job");
        if(!jobStack.empty())
            System.out.println("2 - Delete job");
        System.out.println("*************************");
        int choice = sc.nextInt();
        sc.nextLine();
        System.out.println("*************************");
        switch(choice){
            case 1: System.out.print("New job: ");
                    String currentJob = sc.nextLine();
                    jobStack.push(currentJob);
                    break;

            case 2: System.out.println("Delete job: ");
                    Stack<String> tempStack = new Stack<>();
                    int count = 1;
                    while(!jobStack.empty()){
                        System.out.println(count + " - " + jobStack.peek());
                        tempStack.push(jobStack.pop());
                        count++;
                    }
                    choice = sc.nextInt();
                    count = tempStack.size();
                    while(!tempStack.empty()){
                        if(count!=choice)
                            jobStack.push(tempStack.pop());
                        else
                            tempStack.pop();
                        count--;
                    }
                    break;
            }
            // Update user object
            user.setJobs(jobStack);

            // Update to database
            String jobs = String.join(",", jobStack);
            database.updateUserProfile(user, "jobs", jobs);
        
        System.out.println("*************************");
        return user;
    }

    public boolean isBanned(User user){
        if(user.getBanStartDate().equals(""))
            return false;
        Period duration  = Period.parse(user.getBanDuration());
        LocalDate startDate = LocalDate.parse(user.getBanStartDate());
        Period difference = Period.between(startDate, LocalDate.now());
        if(difference.getDays()>duration.getDays()){
            user.setBanDuration("P0Y0M0D");
            user.setBanStartDate(null);
            database.updateUserProfile(user, "banDuration", user.getBanDuration());
            database.updateUserProfile(user, "banStartDate", user.getBanStartDate());
            return false;
        }else{
            String banDuration = null;
            LocalDate endDate = null;
            if(user.getBanDuration().equals("P1Y")){
                banDuration = "ONE YEAR";
                endDate = startDate.plusYears(1);
            }else if(user.getBanDuration().equals("P6M")){
                banDuration = "SIX MONTHS";
                endDate = startDate.plusMonths(6);
            }else if(user.getBanDuration().equals("P3M")){
                banDuration = "THREE MONTHS";
                endDate = startDate.plusMonths(3);
            }else if(user.getBanDuration().equals("P1M")){
                banDuration = "ONE MONTH";
                endDate = startDate.plusMonths(1);
            }else if(user.getBanDuration().equals("P7D")){
                banDuration = "ONE WEEK";
                endDate = startDate.plusDays(7);
            }
            System.out.println();
            System.out.println("You are banned by admin for " + banDuration + " since " + user.getBanStartDate());
            System.out.println("Remaining banned time: " + getRemainingBannedTime(user, endDate));
            System.out.println("You can start using Facebook again on " + endDate);
            System.out.println();
            return true;
        }
    }

    //To check the remaining banned time of any user
    public String getRemainingBannedTime(User user, LocalDate endDate) {
            LocalDate currentDate = LocalDate.now();
            Period difference = Period.between(currentDate, endDate);
            int year = difference.getYears();
            int month = difference.getMonths();
            int day = difference.getDays();
            String str = "";
            if(year!=0) 
                str += year + " year ";
            if(month!=0){
                str += month;
                if(month>1)
                    str += " months ";
                else
                    str += " month ";
            }
            if(day!=0){
                str += day;
                if(day>1)
                    str += " days";
                else
                    str += " day";
            }
            return str;
    }

    public boolean verifyPassword(String password){
        // Check password length
        if(password.length()<8){
            System.out.println("***Password must be at least 8 characters***");
            return false;
        }

        // Check if password contains space characters
        if(password.contains(" ")){
            System.out.println("***Password must not contain any space***");
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
                System.out.println("***Password must contains at least one uppercase letter***");
            if(!lower)
                System.out.println("***Password must contains at least one lowercase letter***");
            if(!digit)
                System.out.println("***Password must contains at least one digit***");
            if(!special)
                System.out.println("***Password must contains at least one special character***");
            return false;
        }
    }

    public boolean verifyGender(String gender){
        if(gender.toLowerCase().equals("female") || gender.toLowerCase().equals("male"))
            return true;
        else{
            System.out.println("***Invalid input for gender***");
            return false;
        }
    }

    public boolean validateBirthdayFormat(String birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        try {
            LocalDate.parse(birthday, formatter);
            return true; // Format is valid
        } catch (DateTimeParseException e) {
            System.out.println("***Invalid birthday format***");
            return false; // Format is invalid
        }
    }
}
