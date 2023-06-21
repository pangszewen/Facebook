package com.facebook.fullstackbackend;

import com.facebook.fullstackbackend.model.AccountManagement;
import com.facebook.fullstackbackend.model.User;

import java.util.InputMismatchException;
import java.util.Scanner;

public class FullstackBackendApplication {

        public static void main(String[] args) {
                FullstackBackendApplication f  = new FullstackBackendApplication();
                f.startFacebook();
		
        }

        public void startFacebook(){
                Scanner sc = new Scanner(System.in);
                AccountManagement manager = new AccountManagement();
  
                try{
                        int choice1 = 0;
                        while(choice1==0){    
                                User user = null;
                                System.out.println("*************************");
                                System.out.println("\tFacebook");
                                System.out.println("-------------------------");
                                int choice = 0;
                                while(choice!=1 && choice!=2 && choice!=-1){
                                        System.out.println("1 - Register");
                                        System.out.println("2 - Login");
                                        System.out.println("-1 - Terminate the whole program");
                                        System.out.println("*************************");
                                        choice = sc.nextInt();
                                        sc.nextLine();
                                        System.out.println("*************************");
                                
                                        switch(choice){
                                                case 1: manager.registration();
                                                        user = manager.login();
                                                        break;
                                                case 2: user = manager.login();
                                                        break;
                                                case -1: System.exit(0); // Terminate the program
                                                        break;
                                        }  
                                }     
                                if(user!=null){
                                        choice1 = 1;
                                        while(choice1!=0){
                                                try{
                                                        System.out.println("\tHome Page");
                                                        System.out.println("-------------------------");
                                                        System.out.println("0 - Log out");
                                                        System.out.println("1 - My Page");
                                                        System.out.println("2 - Search Facebook");
                                                        System.out.println("3 - Your friends");
                                                        System.out.println("4 - Messenger");
                                                        System.out.println("5 - Suggestions");
                                                        System.out.println("6 - History");
                                                        System.out.println("*************************");
                                                        choice1 = sc.nextInt();
                                                        sc.nextLine();
                                                        System.out.println("*************************");
                                                        switch(choice1){
                                                                case 1 -> manager.viewMyPage();
                                                                case 2 -> manager.searchUsers();
                                                                case 3 -> manager.displayFriends();
                                                                case 4 -> manager.messenger();
                                                                case 5 -> manager.displayRecommendedUsers();
                                                                case 6 -> manager.displayHistory();
                                                        }
                                                }catch(InputMismatchException e){
                                                        System.out.println("*************************");
                                                        System.out.println("Invalid input");
                                                        System.out.println("*************************");
                                                        sc.nextLine();
                                                        continue;
                                                }
                                        }
                                }
                        }
                }catch(InputMismatchException e){
                        System.out.println("*************************");
                        System.out.println("Invalid input");
                        System.out.println("*************************");
                        sc.nextLine();
                        startFacebook();
                }catch(Exception e){
                        System.out.println("*************************");
                        System.out.println("Invalid input");
                        System.out.println("*************************");
                        sc.nextLine();
                        startFacebook();
                }
        }

}
