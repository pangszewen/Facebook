package com.facebook.fullstackbackend.model;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

// Only some methods from the LinkedList class will be used
public class LinkedList<E>{
    Scanner sc = new Scanner(System.in);
    PostManagement postManager = new PostManagement();
    DatabaseSql<String> database = new DatabaseSql<>();
    Node<String> head, tail;
    int size;

    public LinkedList(){
        this.head = null;
        this.tail = null;
        size=0;
    }

    // Method used to add post into history after viewing.
    public void addFirst(String e){
        Node<String> temp = new Node<>(e, head, null);
        if(head!=null)
            head.prev = temp;
        head = temp;
        if(tail==null)
            tail = temp;
        size++;
    }

    public void addLast(String e){
        Node<String> temp = new Node<>(e, null, tail);
        if(tail!=null)
            tail.next = temp;
        tail = temp;
        if(head==null)
            head = temp;
        size++;
    }

    public void add(String e, int index){
        if(index<0 || index>size)   
            throw new IndexOutOfBoundsException();
        else if(index==0)
            addFirst(e);
        else if(index==size)
            addLast(e);
        else{
            Node<String> current = head;
            for(int i=0; i<index; i++)
                current = current.next;
            Node<String> insert = new Node<>(e, current, current.prev);
            current.prev.next = insert;
            current.prev = insert;
            size++;
        }
    }

    public void removeFirst(){
        if(size==0)
            throw new NoSuchElementException();
        head = head.next;
        if(head!=null)
            head.prev = null;
        size--;
    }

    public void removeLast(){
        if(size==0)
            throw new NoSuchElementException();
        tail = tail.prev;
        if(tail!=null)
            tail.next = null;
        size--;
    }

    public void remove(int index){
        if(index<0 || index>=size)
            throw new IndexOutOfBoundsException();
        else if(index==0)
            removeFirst();
        else if(index==size-1)
            removeLast();
        else{
            Node<String> current = head;
            for(int i=0; i<index; i++)
                current = current.next;
            Node<String> temp = current;
            current.next.prev = current.prev;
            current.prev.next = current.next;
            temp.next = null;
            temp.prev = null;
            size--;
        }
    }

    // Method used to remove post from the history.
    public LinkedList<String> remove(String post, LinkedList<String> history){
        if(!history.contains(post))
            throw new NoSuchElementException();
        else if(post.equals(history.head.element))
            removeFirst();
        else if(post.equals(history.tail.element))
            removeLast();
        else{
            Node<String> current = history.head;
            while(current!=null){
                if(current.element.equals(post))
                    break;
                current = current.next;
            }
            Node<String> temp = current;
            current.next.prev = current.prev;
            current.prev.next = current.next;
            temp.next = null;
            temp.prev = null;
            history.size--;
        }
        return history;
    }

    // Method to check if the history contains a specific post.
    public boolean contains(String e){
        if(size==0)
            return false;
        if(head.element.equals(e))
            return true;
        if(tail.element.equals(e))
            return true;
        Node<String> current = head;
        for(int i=1; i<size-1; i++){
            current = current.next;
            if(current.element.equals(e))
                return true;
        }
        return false;
    }

    // Method to display the user's history by iterating it.
    public LinkedList<String> iterateForward(User user, LinkedList<String> history, ConnectionGraph<String> graph){
        try{
            Node<String> current = history.head;
            int choice = 1;
            while(current!=null && choice>=0){
                Post post = database.getPost(current.element);
                User u1 = database.getProfile(post.getUserID());
                if(u1 == null){
                    history = remove(post.getPostID(), history);
                    current = current.next;
                }
                postManager.viewPost(post);

                if(!current.element.equals(history.tail.element))
                    System.out.println("0 - Next");
                System.out.println("1 - View post");
                if(!current.element.equals(history.head.element))
                    System.out.println("2 - Back");
                System.out.println("-1 - Back to Home page");
                System.out.println("*************************");
                choice = sc.nextInt();
                System.out.println("*************************");
                switch(choice){
                    case 0: if(!current.element.equals(history.tail.element))
                                current = current.next;
                            break;
                    case 1: Node<String> temp = current.prev;
                            // Condition that if the user has already viewed a friend's private post before removing the friend
                            // After the user has removed the friend, when the user look back his history, he wont be able to react the removed friend's private post (because they are not friends already)
                            // The user can view the post in history, but not able to like or comment
                            if(post.getStatus().equals(Post.Status.PRIVATE)){
                                if(database.privacy(user, u1, graph))
                                    postManager.displayPostAction(user, post, history);
                                else{
                                    System.out.println("This post is private.");
                                    System.out.println("-------------------------");
                                }
                            }else
                                postManager.displayPostAction(user, post, history);

                        
                            if(!database.verifyPostExist(post)){
                                if(temp!=null)
                                    current = temp;     // If the history post has been deleted, return to the previous history post
                                else
                                    current = current.next;     // If there is no previous history, return to the next history post
                            }
                            break;
                    case 2: if(!current.element.equals(history.head.element))
                                current = current.prev;
                            break;
                }
            }
            return history;
        }catch(InputMismatchException e){
            System.out.println("*************************");
            System.out.println("Invalid input");
            System.out.println("*************************");
            sc.nextLine();
            iterateForward(user, history, graph);
        }
        System.out.println("Failed to get history");
        return history;
    }

    public void iterateBackward(){
        System.out.println("iterating backward..");
        Node<String> current = tail;
        while(current!=null){
            System.out.print(current.element + " ");
            current = current.prev;
        }
        System.out.println();
    }

    public int getSize(){
        return size;
    }

    public void clear(){
        Node<String> temp = head;
        int n = size;
        while(head!=null){
            temp = head.next;
            head.next = head.prev = null;
            head = temp;
        }
        temp = null;
        tail.next = tail.prev = null;
        size=0;
        System.out.println("successfully clear " + n + " node(s)");
    }
}

class Node <E>{
    public E element;
    Node<E> next;
    Node<E> prev;

    public Node(E element, Node<E> next, Node<E> prev){
        this.element = element;
        this.next = next;
        this.prev = prev;
    }

    public Node(E element){
        this(element, null, null);
    }
}
