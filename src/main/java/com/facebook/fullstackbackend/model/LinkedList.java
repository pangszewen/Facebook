package com.facebook.fullstackbackend.model;

import java.util.NoSuchElementException;
import java.util.Scanner;

import com.facebook.fullstackbackend.repository.DatabaseSql;

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

    public void addFirst(String e){
        Node<String> temp = new Node<>(e, head, null);
        if(head!=null)
            head.prev = temp;
        head = temp;
        if(tail==null)
            tail = temp;
        size++;
        //System.out.println("adding: " + e);
    }

    public void addLast(String e){
        Node<String> temp = new Node<>(e, null, tail);
        if(tail!=null)
            tail.next = temp;
        tail = temp;
        if(head==null)
            head = temp;
        size++;
        //System.out.println("adding: " + e);
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
            //System.out.println("adding: " + e);
        }
    }

    public void removeFirst(){
        if(size==0)
            throw new NoSuchElementException();
        head = head.next;
        if(head!=null)
            head.prev = null;
        size--;
        //System.out.println("deleted: " + temp.element);
    }

    public void removeLast(){
        if(size==0)
            throw new NoSuchElementException();
        tail = tail.prev;
        if(tail!=null)
            tail.next = null;
        size--;
        //System.out.println("deleted: " + temp.element);
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
            //System.out.println("deleted: " + temp.element);
        }
    }

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
            //System.out.println("deleted: " + temp.element);
        }
        return history;
    }

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

    public LinkedList<String> iterateForward(User user, LinkedList<String> history, ConnectionGraph<String> graph){
        //System.out.println("iterating forward..");
        Node<String> current = history.head;
        int choice = 1;
        while(current!=null && choice>=0){
            Post post = database.getPost(current.element);
            User u1 = database.getProfile(post.getUserID());
            postManager.viewPost(post);
            if(current.element!=history.tail.element){
                System.out.println(current.next.element);
                System.out.println("0 - Next");
            }
            System.out.println("1 - View post");
            if(current!=history.head)
                System.out.println("2 - Back");
            System.out.println("-1 - Back to home page");
            System.out.println("*************************");
            choice = sc.nextInt();
            System.out.println("*************************");
            switch(choice){
                case 0: if(current.element!=history.tail.element)
                            current = current.next;
                        break;
                case 1: Node<String> temp = current.prev;
                        if(post.getStatus().equals(Post.Status.PRIVATE)){
                            if(database.privacy(user, u1, graph))
                                postManager.displayPostAction(user, post, history);
                            else{
                                System.out.println("This post is private.");
                                System.out.println("-------------------------");
                            }
                        }else
                            postManager.displayPostAction(user, post, history);

                        // If the history post has been deleted, return to the previous history post
                        if(!database.verifyPostExist(post)){
                            current = temp;
                        }
                        break;
                case 2: if(current!=head)
                            current = current.prev;
                        break;
            }
        }
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
