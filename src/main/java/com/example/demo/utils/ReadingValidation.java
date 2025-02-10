package com.example.demo.utils;

import com.example.demo.models.Customer;
import com.example.demo.models.Reading;
import jakarta.validation.ValidationException;

public class ReadingValidation {
    public void validate(Reading reading) throws ValidationException {
        if (reading.getCustomer() == null) {
            throw new ValidationException("customer is required");
        }
        if (reading.getDateOfReading() == null) {
            throw new ValidationException("dateOfReading is required");
        }
        if (reading.getMeterId() == null || reading.getMeterId().isEmpty()) {
            throw new ValidationException("meterId is required");
        }
        if (reading.getSubstitute() == null) {
            throw new ValidationException("substitute is required");
        }
        if (reading.getMeterCount() == null) {
            throw new ValidationException("meterCount is required");
        }
        if (reading.getKindOfMeter() == null || reading.getKindOfMeter().toString().isEmpty()) {
            throw new ValidationException("kindOfMeter is required");
        }
    }
}
