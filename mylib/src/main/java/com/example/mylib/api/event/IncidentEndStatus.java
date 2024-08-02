package com.example.mylib.api.event;

public class IncidentEndStatus {

    public String status;
    public String status_message;

    public IncidentEndStatus(String status, String status_message) {
        this.status = status;
        this.status_message = status_message;
    }
}
