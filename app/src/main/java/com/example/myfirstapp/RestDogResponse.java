package com.example.myfirstapp;

import java.util.List;

public class RestDogResponse {
    private List<Dogs> message;
    private String status;

    public List<Dogs> getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
