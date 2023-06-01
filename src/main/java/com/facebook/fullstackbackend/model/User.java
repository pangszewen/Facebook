package com.facebook.fullstackbackend.model;

import java.util.ArrayList;
import java.util.Stack;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class User {
    DatabaseSql<String> database;
    private String accountID;
    private String username;
    private String email;
    private String phoneNo;
    private String password;
    private String role;
    private String name;
    private String birthday;
    private int age;
    private String address;
    private Gender gender;
    private String status;
    private int noOfFriends;
    private ArrayList<String> hobbies;
    private Stack<String> jobs;
    private Stack<String> requestList;
    private int noOfPost;
    enum Gender{
        MALE,
        FEMALE
    }

    public User(UserBuilder builder){
        this.accountID = builder.accountID;
        this.username = builder.username;
        this.email = builder.email;
        this.phoneNo = builder.phoneNo;
        this.password = builder.password;
        this.role = builder.role;
        this.name = builder.name;
        this.birthday = builder.birthday;
        this.age = builder.age;
        this.address = builder.address;
        this.gender = builder.gender;
        this.status = builder.status;
        this.noOfFriends = builder.noOfFriends;
        this.hobbies = builder.hobbies;
        this.jobs = builder.jobs;
        this.requestList = builder.requestList;
        this.noOfPost = builder.noOfPost;
    }

    public void setAccountID(){
        this.accountID = String.valueOf(database.generateAccountID());
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

    public void setGender(String gender){
        this.gender = Gender.valueOf(gender.toUpperCase());
    }
    public void setGender(User.Gender gender){
        this.gender = gender;
    }
    public Gender getGender(){
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

    public void setHobbies(String hobbies){
        this.hobbies.add(hobbies);
    }
    public void setHobbies(ArrayList<String> hobbies){
        this.hobbies = hobbies;
    }
    public ArrayList<String> getHobbies(){
        return hobbies;
    }

    public void setJobs(String job){
        this.jobs.push(job);
    }
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

    public void setNoOfPost(int noOfPost){
        this.noOfPost = noOfPost;
    }
    public int getNoOfPost(){
        return noOfPost;
    }

}
