package com.facebook.fullstackbackend.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

// The methods in this class is exclusive for admin users only
public class Admin {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
    UserBuilder builder = new UserBuilder();

    public User setAdmin(User user){
        user.setRole("admin");
        database.updateUserAccount(user, "role", user.getRole());
        database.updateUserProfile(user, "role", user.getRole());
        System.out.println("***" + user.getName() + " is now an admin***");
        System.out.println("*************************");
        return user;
    }

    // Check if the given user is an admin
    public boolean isAdmin(User user){
        if(user.getRole().equalsIgnoreCase("admin"))
            return true;
        else
            return false;
    }

    public ConnectionGraph<String> deleteAccount(User user, ConnectionGraph<String> graph){
        if(isAdmin(user)){
            System.out.println("***This user is an admin. Operation denied***");
            return graph;
        }
        database.deleteAccount(user);
        graph.removeVertex(graph, user.getUsername());
        return graph;
    }

    public void banUser(User user) {
        // Banning admin users are not allowed
        if(isAdmin(user)){
            System.out.println("***This user is an admin. Operation denied***");
            return;
        }
        int choice = 6;
        while(choice<0 || choice>5){
            System.out.println("Select ban duration:");
            System.out.println("1 - One week");
            System.out.println("2 - One month");
            System.out.println("3 - Three months");
            System.out.println("4 - Six months");
            System.out.println("5 - One year");
            System.out.println("0 - Back");
            System.out.println("*************************");
            choice = sc.nextInt();
            sc.nextLine();
            System.out.println("*************************");
            Period duration = null;
            switch(choice){
                case 1 -> duration = Period.ofWeeks(1);
                case 2 -> duration = Period.ofMonths(1); 
                case 3 -> duration = Period.ofMonths(3);
                case 4 -> duration = Period.ofMonths(6);
                case 5 -> duration = Period.ofYears(1);
            }
            if(choice>0 && choice<6){
                user.setBanDuration(String.valueOf(duration));
                user.setBanStartDate(String.valueOf(LocalDate.now()));
                database.updateUserProfile(user, "banDuration", user.getBanDuration());
                database.updateUserProfile(user, "banStartDate", user.getBanStartDate());
            }
        }
    }

    //To check the remaining banned time of any user
    public String getRemainingBannedTime(User user) {
        if(builder.isBanned(user)){
            LocalDate currentDate = LocalDate.now();
            Period banDuration = Period.parse(user.getBanDuration());
            Period difference = Period.between(currentDate, LocalDate.parse(user.getBanStartDate()));
            int year = banDuration.getYears()-difference.getYears();
            int month = banDuration.getMonths()-difference.getMonths();
            int day = banDuration.getDays()-difference.getDays();
            String str = "";
            if(year!=0)
                str += year + "year ";
            if(month!=0)
                str += month;
                if(month>1)
                    str += "months ";
                else
                    str += "month ";
            if(day!=0)
                str += day;
                if(day>1)
                    str += "days";
                else
                    str += "day";
            return str;
        }else
            return null;
    }

    // Content is manually removed by the admin
    public void manuallyRemoveInappropriateContent(Post post, User user){        
        database.deletePost(post);
        user.setNoOfDeletedPost(user.getNoOfDeletedPost()+1);
        DatabaseSql<Integer> databaseInt = new DatabaseSql<>();
        databaseInt.updateUserProfile(user, "noOfDeletedPost", user.getNoOfDeletedPost());
    }

    public void updateProhibitedWord(){
        System.out.print("Enter new prohibited words: ");
        String newWord = sc.nextLine();
        System.out.println("*************************");
        database.addNewProhibitedWord(newWord);
    }



}
