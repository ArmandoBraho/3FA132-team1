package com.example.demo.models;

import java.sql.Date;

public class Client {
    private String uuid;
    private String title;
    private String firstName;
    private String lastName;
    private Date birthDate;
    // todo: should they be final??

    // Constructor
    public Client(String uuid, String title, String firstName, String lastName, Date birthDate) {
        this.uuid = uuid;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    // Getters
    public String getUuid() { return uuid; }
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Date getBirthDate() { return birthDate; }
    // todo: ask if lombok is allowed(library for getters and setters) and if yes use it and delete getters and setters
}
