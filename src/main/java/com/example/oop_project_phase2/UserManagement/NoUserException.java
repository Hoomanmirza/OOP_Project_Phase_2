package com.example.oop_project_phase2.UserManagement;

public class NoUserException extends Exception{
    public NoUserException(){}
    public NoUserException(String message){
        super(message);
    }
}