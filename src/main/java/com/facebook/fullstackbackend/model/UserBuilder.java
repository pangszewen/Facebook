package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.Stack;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class UserBuilder {
    DatabaseSql<String> database = new DatabaseSql<>();
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
    public char gender;
    public String status;
    public int noOfFriends;
    public ArrayList<String> hobbies = new ArrayList<>();
    public Stack<String> jobs = new Stack<>();
    public Stack<String> requestList = new Stack<>();
    
    public UserBuilder(){
        this.accountID = null;
        this.username = null;
        this.email = null;
        this.phoneNo = null;
        this.password = null;
        this.role = "normal";
    }

    public UserBuilder(String accountID, String username, String email, String phoneNo, String password, String role){
        this.accountID = accountID;
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.role = "normal";
    }

    public void setAccountID(){
        this.accountID = String.valueOf(database.generateAccountID());
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

    public void setGender(char gender){
        this.gender = gender;
    }
    public char getGender(){
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

    // to create User object
    public User build(){
        return new User(this);
    }
}
