package com.example.demo.utils;

import com.example.demo.models.Customer;
import jakarta.validation.ValidationException;

public class CustomerValidation {
    public void validate(Customer customer) throws ValidationException {
        if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
            throw new ValidationException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
            throw new ValidationException("Last name is required");
        }
        if (customer.getGender() == null) {
            throw new ValidationException("gender is required");
        }
    }
}