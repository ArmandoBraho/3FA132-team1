package com.example.demo.models;

import java.time.LocalDate;
import java.util.UUID;

public class Customer implements com.example.demo.interfaces.ICustomer {
    private UUID id;
    private Gender gender;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    public Customer(UUID id, Gender gender, String firstName, String lastName, LocalDate birthDate) {
        this.id = id;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
