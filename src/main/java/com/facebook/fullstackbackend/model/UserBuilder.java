package com.facebook.fullstackbackend.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

import org.hibernate.internal.build.AllowSysOut;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class UserBuilder {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
    DatabaseSql<Integer> databaseInt = new DatabaseSql<>();

    // Total 22 attributes
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
        this.accountID = database.generateID("useraccount", "accountID");
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
        // Account ID is auto-generated.
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

    // Getter and setter method.
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
    // Setter method used for user input and create user object.
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

    // Setter method used for edit user profile.
    public void setHobbies(String hobbies){
        this.hobbies.add(hobbies);
    }
    // Setter method used for create user object.
    public void setHobbies(ArrayList<String> hobbies){
        this.hobbies = hobbies;
    }
    public ArrayList<String> getHobbies(){
        return hobbies;
    }

    // Setter method used for edit user profile.
    public void setJobs(String job){
        this.jobs.push(job);
    }
    // Setter method used for create user object.
    public void setJobs(Stack<String> jobs){
        this.jobs = new Stack<>();
        this.jobs = jobs;
    }
    public Stack<String> getJobs(){
        return jobs;
    }

    // Setter method used for adding friend requests.
    public void setRequestList(String username){
        this.requestList.push(username);
    }
    // Setter method used for create user object.
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

    // Setter method used for adding group when joining a group.
    public void setGroups(String group){
        this.groups.add(group);
    }
    // Setter method used for create user object.
    public void setGroups(ArrayList<String> groups){
        this.groups = groups;
    }
    public ArrayList<String> getGroups(){
        return groups;
    }

    // Setter method used to send group invitation to a user. This method is used when inviting group member-to-be.
    public void setGroupInvitations(User inviter, Group group){
        String str = inviter.getAccountID() + ":" + group.getGroupID();
        this.groupInvitations.add(str);
    }
    // Setter method used for create user object.
    public void setGroupInvitations(ArrayList<String> groupInvitations){
        this.groupInvitations = groupInvitations;
    }
    public ArrayList<String> getGroupInvitations(){
        return groupInvitations;
    }

    // Method to create User object.
    public User build(){
        return new User(this);
    }

    // Method to edit user's password.
    public User editPassword(User user){
        try{
            user = database.getAccount(user.getUsername());
            // For verification purpose, user must be able to enter his old password correctly.
            System.out.print("Enter your old password: ");
            String oldPassword = sc.nextLine();
            System.out.println("*************************");
            while(!database.hashPassword(oldPassword).equals(user.getPassword())){
                System.out.println("Wrong password. Please enter again.");
                System.out.print("Old password: ");
                oldPassword = sc.nextLine();
                System.out.println("*************************");
            }

            System.out.print("Enter your new password: ");
            String newPassword = sc.nextLine();
            System.out.println("*************************");
            while(!verifyPassword(newPassword)){   // Check strength of password.
                System.out.print("New password: ");
                newPassword = sc.nextLine();
                System.out.println("*************************");
            }

            int choice = 2;
            while(choice!=0 && choice!=1){
                System.out.println("0 - Back");
                System.out.println("1 - Confirm changes");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }
            if(choice==1){
                // Hashing the new password.
                newPassword = database.hashPassword(newPassword);

                // Update user object.
                user.setPassword(newPassword);

                // Update to database.
                database.updateUserAccount(user, "password", newPassword);
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editPassword(user);
        }
        System.out.println("Failed to edit password");
        return user;
    }

    // Method to edit user's name.
    public User editName(User user){
        try{
            // Display current name and prompt user to enter the new name.
            System.out.println("Current name: " + user.getName());
            System.out.print("Change name to: ");
            String name = sc.nextLine();
            System.out.println("*************************");
            
            int choice = 2;
            while(choice!=0 && choice!=1){
                System.out.println("0 - Back");
                System.out.println("1 - Confirm changes");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }
            if(choice==1){
                // Update user object
                user.setName(name);

                // Update to database
                database.updateUserProfile(user, "name", name);
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editName(user);
        }
        System.out.println("Failed to edit name");
        return user;
    }

    // Method to edit user's birthday and age.
    public User editBirthday(User user){
        try{
            // Display current birthday and prompt user to enter new birthday.
            System.out.println("Current birthday: " + user.getBirthday());
            System.out.print("Change birthday to: ");
            String birthday = sc.nextLine();
            System.out.println("*************************");
            while(!validateBirthdayFormat(birthday)){   // Validation of birthday input format.
                System.out.print("Change birthday to (format: YYYY-MM-DD): ");
                birthday = sc.nextLine();
                System.out.println("*************************");
            }

            int choice = 2;
            while(choice!=0 && choice!=1){
                System.out.println("0 - Back");
                System.out.println("1 - Confirm changes");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }
            if(choice==1){
                // Update user object.
                user.setBirthday(birthday);

                // Update age based on birthday.
                LocalDate currentDate = LocalDate.now();
                Period period = Period.between(LocalDate.parse(birthday), currentDate);
                int age = period.getYears();
                user.setAge(age);

                // Update to database.
                database.updateUserProfile(user, "birthday", birthday);
                databaseInt.updateUserProfile(user, "age", age);
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editBirthday(user);
        }
        System.out.println("Failed to edit birthday and age");
        return user;
    }

    // Method to edit user's address.
    public User editAddress(User user){
        try{
            // Display current address and prompt user to enter new address.
            System.out.println("Current address: " + user.getAddress());
            System.out.print("Change address to: ");
            String address = sc.nextLine();
            System.out.println("*************************");

            int choice = 2;
            while(choice!=0 && choice!=1){
                System.out.println("0 - Back");
                System.out.println("1 - Confirm changes");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }
            if(choice==1){
                // Update user object.
                user.setAddress(address);

                // Update to database.
                database.updateUserProfile(user, "address", address);
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editAddress(user);
        }
        System.out.println("Failed to edit address");
        return user;
    }

    // Method to edit user's gender.
    public User editGender(User user){
        try{
            // Display current gender and prompt user to enter gender (female/male).
            System.out.println("Current gender: " + user.getGender());
            System.out.print("Change gender to (MALE/FEMALE): ");
            String gender = sc.nextLine();
            System.out.println("*************************");

            int choice = 2;
            while(choice!=0 && choice!=1){
                System.out.println("0 - Back");
                System.out.println("1 - Confirm changes");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }
            if(choice==1){
                // Update user object
                user.setGender(gender);

                // Update to database
                database.updateUserProfile(user, "gender", gender);
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editGender(user);
        }
        System.out.println("Failed to edit gender");
        return user;
    }

    // Method to edit user's relationship status.
    public User editStatus(User user){
        try{
            // Display the current relationship status and prompt user to enter new relationship status.
            if(user.getStatus().length()>0){
                System.out.println("Current relationship status: " + user.getStatus());
                System.out.print("Change status to: ");
            }else   // Condition if the user's relationship status is not applicable.
                System.out.print("Relationship status: ");
            String status = sc.nextLine();
            System.out.println("*************************");

            int choice = 2;
            while(choice!=0 && choice!=1){
                System.out.println("0 - Back");
                System.out.println("1 - Confirm changes");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
            }
            if(choice==1){
                // Update user object.
                user.setStatus(status);

                // Update to database.
                database.updateUserProfile(user, "status", status);
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editStatus(user);
        }
        System.out.println("Failed to edit relationship status");
        return user;
    }

    // Method to edit user's hobbies.
    public User editHobbies(User user){
        try{
            int choice = 1;
            while(choice!=0){
                // Display current hobby and prompt user to select whether to add, change or delete hobby.
                System.out.println("Current hobby: " + user.getHobbies().get(0));
                System.out.println("0 - Back");
                System.out.println("1 - Add hobby");
                System.out.println("2 - Change hobby");
                System.out.println("3 - Delete hobby");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                switch(choice){
                    case 1: System.out.print("New hobby: ");
                            String hobby = sc.nextLine();
                            System.out.println("*************************");
                            // Add new hobby to user object.
                            user.getHobbies().add(hobby);
                            break;

                    case 2: while(choice!=0){
                                System.out.println("Select hobby: ");
                                System.out.println("0 - Back");
                                for(int i=0; i<user.getHobbies().size(); i++){
                                    System.out.println((i+1) + " - " + user.getHobbies().get(i));
                                }
                                System.out.println("*************************");
                                choice = sc.nextInt();
                                System.out.println("*************************");

                                // Change the desired hobby to be in index 0 and update user object.
                                if(choice!=0){
                                    String temp = user.getHobbies().get(choice-1);
                                    user.getHobbies().set(choice-1, user.getHobbies().get(0));
                                    user.getHobbies().set(0, temp);
                                }
                            }
                            if(choice==0)
                                choice = 1;     // Maintain in loop.
                            break;

                    case 3: while(choice!=0){
                                System.out.println("Delete hobby: ");
                                System.out.println("0 - Back");
                                for(int i=0; i<user.getHobbies().size(); i++){
                                    System.out.println((i+1) + " - " + user.getHobbies().get(i));
                                }
                                System.out.println("*************************");
                                choice = sc.nextInt();
                                System.out.println("*************************");

                                // Delete hobby from user object.
                                if(choice!=0)
                                    user.getHobbies().remove(choice-1);
                            }
                            if(choice==0)
                                choice = 1;     // Maintain in loop.
                            break;
                }
                // Update to database.
                String hobbies = String.join(",", user.getHobbies());
                database.updateUserProfile(user, "hobbies", hobbies);
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editHobbies(user);
        }
        System.out.println("Failed to edit hobbies");
        return user;
    }

    // Method to edit user's jobs.
    public User editJobs(User user){
        try{
            int choice = 1;
            while(choice!=0){
                Stack<String> jobStack = user.getJobs();

                // Display current job and previous job (if available) and prompt user to select whether to add or delete job.
                if(!jobStack.empty()){
                    String currentJob = jobStack.pop();
                    System.out.println("Current job: " + currentJob);
                    // Only display if previous job is available.
                    if(!jobStack.empty())
                        System.out.println("Previous job: " + jobStack.peek());
                    jobStack.push(currentJob);  // Restore the job stack
                }
                System.out.println("0 - Back");
                System.out.println("1 - Add job");
                if(!jobStack.empty())
                    System.out.println("2 - Delete job");
                System.out.println("*************************");
                choice = sc.nextInt();
                sc.nextLine();
                System.out.println("*************************");
                switch(choice){
                    case 1: System.out.print("New job: ");
                            String currentJob = sc.nextLine();
                            System.out.println("*************************");
                            choice = 2;
                            while(choice!=0 && choice!=1){
                                System.out.println("0 - Back");
                                System.out.println("1 - Confirm changes");
                                System.out.println("*************************");
                                choice = sc.nextInt();
                                System.out.println("*************************");
                            }
                            if(choice==1)
                                jobStack.push(currentJob);  // Push new job into the stack
                            if(choice==0)
                                choice = 1;     // Maintain in loop
                            break;

                    case 2: while(choice!=0){
                                System.out.println("Delete job: ");
                                Stack<String> tempStack = new Stack<>();
                                Stack<String> stack = new Stack<>();
                                stack.addAll(jobStack);
                                int count = 1;
                                System.out.println("0 - Back");
                                // Display all user's job by printing the latest job first.
                                while(!jobStack.empty()){
                                    System.out.println(count + " - " + jobStack.peek());
                                    tempStack.push(jobStack.pop());
                                    count++;
                                }
                                System.out.println("*************************");
                                choice = sc.nextInt();
                                sc.nextLine();
                                System.out.println("*************************");
                                System.out.println(stack);
                                //If input is in range, proceed to delete the selected job.
                                if(choice>0 && choice<tempStack.size()+1){
                                    jobStack = new Stack<>();
                                    count = tempStack.size();
                                    while(!tempStack.empty()){
                                        if(count!=choice){
                                            jobStack.push(tempStack.pop());
                                        }else
                                            tempStack.pop();
                                        count--;
                                    }
                                }else if(choice==0){
                                    jobStack = stack;
                                }
                            }
                            break;
                }
                // Update user object
                user.setJobs(jobStack);

                // Update to database
                String jobs = String.join(",", user.getJobs());
                database.updateUserProfile(user, "jobs", jobs);
            }
            return user;
        }catch(InputMismatchException e ){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            editJobs(user);
        }
        System.out.println("Failed to edit jobs");
        return user;
    }

    // Method to check if the user is banned from using Facebook.
    public boolean isBanned(User user){
        if(user.getBanStartDate().equals(""))
            return false;

        Period duration  = Period.parse(user.getBanDuration());
        LocalDate startDate = LocalDate.parse(user.getBanStartDate());
        Period difference = Period.between(startDate, LocalDate.now());
        
        if(difference.getDays()>duration.getDays()){    // User's ban duration is over, reset the user's ban duration and ban start date.
            user.setBanDuration("P0Y0M0D");
            user.setBanStartDate("");
            database.updateUserProfile(user, "banDuration", user.getBanDuration());
            database.updateUserProfile(user, "banStartDate", user.getBanStartDate());
            return false;
        }else{      // User is still banned from using Facebook
            String banDuration = null;
            LocalDate endDate = null;
            // Get the end date of ban duration from start date.
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

    // Method to check the remaining banned time of any user
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

    // Method to test the strength of password.
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

    // Method to verify the validation of user input for gender.
    public boolean verifyGender(String gender){
        if(gender.toLowerCase().equals("female") || gender.toLowerCase().equals("male"))
            return true;
        else{
            System.out.println("***Invalid input for gender***");
            return false;
        }
    }

    // Method to verify the validation of user input for birthday.
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
