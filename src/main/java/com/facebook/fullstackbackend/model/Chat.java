package com.facebook.fullstackbackend.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Chat {
    Scanner sc = new Scanner(System.in);
    private String pathToPackage = "C:\\Users\\HP PAVILION\\Documents\\UM Y1 S2\\DS\\fullstack-backend\\src\\main\\java\\com\\facebook\\fullstackbackend\\repository\\Chats\\";

    public void createUserChat(User user, User u1){
        String fileName = pathToPackage + nameUserChat(user, u1);
        createChat(fileName);
    }

    public void createGroupChat(Group group){
        String fileName = pathToPackage + nameGroupChat(group);
        createChat(fileName);
    }

    public void createChat(String fileName){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the text file.");
            e.printStackTrace();
        }
    }

    public void readUserChat (User user, User u1){
        String fileName = pathToPackage + nameUserChat(user, u1);
        readChat(fileName);
    }

    public void readGroupChat(Group group){
        String fileName = pathToPackage + nameGroupChat(group);
        readChat(fileName);
    }

    public void readChat(String fileName){
        String previousDate = null;
        String date = null;
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                date = line.substring(0, 10);
                if(!date.equals(previousDate)){
                    System.out.println("<" + date + ">");
                }
                String out;
                //System.out.println(line);
                out = line.substring(11);
                System.out.println(out);
                previousDate = date;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred while reading the text file.");
            e.printStackTrace();
        }
    }

    public void updateUserChat (User user, User u1){
        String fileName = pathToPackage + nameUserChat(user, u1);
        updateChat(user, fileName);
    }

    public void updateGroupChat (User user, Group group){
        String fileName = pathToPackage + nameGroupChat(group);
        updateChat(user, fileName);
    }

    public void updateChat(User user, String fileName){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            String in = "";
            System.out.println("Enter \"/n\" to stop the chat");
            System.out.print(user.getUsername()+": ");
            in = sc.nextLine();
            while (!in.equals("/n")){
                String line = user.getUsername() + ": " + in + " ";
                LocalDateTime dateTime = LocalDateTime.now();
                String chat = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Format LocalDateTime without fractional seconds
                chat += " "+line;
                writer.write(chat+"\n");
                System.out.print(user.getUsername()+": ");
                in = sc.nextLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the text file.");
            e.printStackTrace();
        }
    }

    public String nameUserChat (User user, User u1){
        int id1 = Integer.parseInt(user.getAccountID());
        int id2 = Integer.parseInt(u1.getAccountID());
        String tfName;
        if (id1>id2){
            tfName = Integer.toString(id2)+"C"+Integer.toString((id1));
        }
        else{
            tfName = Integer.toString(id1)+"C"+Integer.toString((id2));
        }
        String fileName = tfName + ".txt";
        return fileName;
    }

    public String nameGroupChat(Group group){
        String fileName = group.getGroupID() + ".txt";
        return fileName;
    }

    public boolean verifyUserChatExist(User user, User u1){
        String fileName = nameUserChat(user, u1);
        File file = new File(fileName);
        if(file.exists())
            return true;
        else    
            return false;
    }

    public boolean verifyGroupChatExist(Group group){
        String fileName = nameGroupChat(group);
        File file = new File(fileName);
        if(file.exists())
            return true;
        else    
            return false;
    }
}
