package com.facebook.fullstackbackend.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

public class Chat {
    Scanner sc = new Scanner(System.in);
    DatabaseSql<String> database = new DatabaseSql<>();
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
        readChat(nameUserChat(user, u1));
    }

    public void readGroupChat(Group group){
        readChat(nameGroupChat(group));
    }

    public void readChat(String fileName){
        fileName = pathToPackage + fileName;
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

    public void displayUserChatName(String textFileName, User user){
        String textFile = textFileName.substring(0, textFileName.length()-4);
        String[] arr = textFile.split("C");
        if(arr[0].equals(user.getAccountID()))
            System.out.println(database.getProfile(arr[1]).getName());
        else
            System.out.println(database.getProfile(arr[0]).getName());
        System.out.println("-------------------------");
    }

    public void displayGroupChatName(String textFileName){
        String textFile = textFileName.substring(0, textFileName.length()-4);
        System.out.println(database.getGroup(textFile).getGroupName());
        System.out.println("-------------------------");
    }

    public void updateUserChat (User user, User u1){
        updateChat(user, nameUserChat(user, u1));
    }

    public void updateGroupChat (User user, Group group){
        updateChat(user, nameGroupChat(group));
    }

    public void updateChat(User user, String fileName){
        fileName = pathToPackage + fileName;
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

    public ArrayList<String> getUserChats(User user){
        String packagePath = "C:\\Users\\HP PAVILION\\Documents\\UM Y1 S2\\DS\\fullstack-backend\\src\\main\\java\\com\\facebook\\fullstackbackend\\repository\\Chats";
        File directory = new File(packagePath);
        File[] files = directory.listFiles();
        ArrayList<String> textFiles = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            boolean isFile = file.isFile();
            boolean containsAccountID = fileName.contains(user.getAccountID());
            boolean containsGroupName = user.getGroups().contains(fileName.subSequence(0, fileName.length() - 4));
            boolean doesNotContainC = !fileName.contains("C");
            if (isFile && (containsAccountID || (containsGroupName && doesNotContainC))) {
                textFiles.add(file.getName());
            }
        }

        ArrayList<String> arrangedTextFiles = dateTimeComparison(textFiles);
        return arrangedTextFiles;        
    }

    public void displayAllChats(ArrayList<String> arrangedTextFiles, User user){
        for(int i=0; i<arrangedTextFiles.size(); i++){
            String textFilesName = arrangedTextFiles.get(i).substring(0, arrangedTextFiles.get(i).length()-4);
            if(arrangedTextFiles.get(i).contains("C")){
                String[] arr = textFilesName.split("C");
                if(arr[0].equals(user.getAccountID()))
                    System.out.println((i+1) + " - " + database.getProfile(arr[1]).getName());
                else
                    System.out.println((i+1) + " - " + database.getProfile(arr[0]).getName());
            }else{
                System.out.println((i+1) + " - " + database.getGroup(textFilesName).getGroupName());
            }
        }
    }

    public ArrayList<String> dateTimeComparison(ArrayList<String> textFiles) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(int i=1; i<textFiles.size(); i++){
                // Parse the string into a LocalDateTime object
                String filePath1 = pathToPackage + textFiles.get(i);
                String date1 = getLastDateOfFile(filePath1);
                LocalDateTime dateTime1 = LocalDateTime.MIN;
                if(date1!=null){
                    dateTime1 = LocalDateTime.parse(getLastDateOfFile(filePath1), formatter); 
                }

            for(int j=0; j<i; j++){
                // Parse the string into a LocalDateTime object
                String filePath2 = pathToPackage + textFiles.get(j);
                String date2 = getLastDateOfFile(filePath2);
                LocalDateTime dateTime2 = LocalDateTime.MIN;
                if(date2!=null){
                    dateTime2 = LocalDateTime.parse(getLastDateOfFile(filePath2), formatter); 
                }

                // Compare the dates
                int comparison = dateTime1.compareTo(dateTime2);
                
                if(comparison > 0) {
                    String temp = textFiles.get(i);
                    textFiles.set(i, textFiles.get(j));
                    textFiles.set(j, temp);
                }
            }
        }
        return textFiles;
    }

    public String getLastDateOfFile(String filePath) {
        String lastLine = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(lastLine!=null)
            lastLine = lastLine.substring(0, 19);
        return lastLine;
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
