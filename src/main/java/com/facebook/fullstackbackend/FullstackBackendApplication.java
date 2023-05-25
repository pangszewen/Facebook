package com.facebook.fullstackbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.facebook.fullstackbackend.model.AccountManagement;
import com.facebook.fullstackbackend.model.User;

import java.util.Scanner;

@SpringBootApplication
public class FullstackBackendApplication {

        public static void main(String[] args) {
	        SpringApplication.run(FullstackBackendApplication.class, args);
		Scanner sc = new Scanner(System.in);
                AccountManagement manager = new AccountManagement();
                User user = null;

                System.out.println("\tFacebook");
                System.out.println("------------------------------");
                System.out.println("1 - Register");
                System.out.println("2 - Login");
                int choice = sc.nextInt();
                switch(choice){
                        case 1: manager.registration();
                                user = manager.login();
                                break;
                        case 2: user = manager.login();
                                break;
                }       
                if(user!=null){
                int choice1 = 1;
                while(choice1>0){
                        System.out.println("0 - Log out");
                        System.out.println("1 - Search friend");
                        System.out.println("2 - Friend requests");
                        System.out.println("3 - Your friends");
                        System.out.println("4 - Suggestions");
                        System.out.println("*************************");
                        choice1 = sc.nextInt();
                        sc.nextLine();
                        System.out.println("*************************");
                        switch(choice1){
                        case 1: manager.searchFriend();
                                break;
                        case 2: manager.displayRequest();
                                break;
                        case 3: manager.displayFriends();
                                break;
                        case 4: manager.displayRecommendedUsers();
                                break;
                }
            }
        }
}

}
