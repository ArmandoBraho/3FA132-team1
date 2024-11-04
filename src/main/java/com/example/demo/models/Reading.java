package com.example.demo.models;

import com.example.demo.interfaces.IReading;

import java.time.LocalDate;
import java.util.UUID;

public class Reading implements IReading {

    private UUID customerUUID;
    private String meterId;
    private LocalDate dateOfReading;
    private Double meterCount;
    private String comment;
    private KindOfMeter kindOfMeter;
    private Boolean substitute;

    public Reading(UUID customerUUID, String meterId, LocalDate dateOfReading, Double meterCount, String comment, KindOfMeter kindOfMeter, Boolean substitute) {
        this.customerUUID = customerUUID;
        this.meterId = meterId;
        this.dateOfReading = dateOfReading;
        this.meterCount = meterCount;
        this.comment = comment;
        this.kindOfMeter = kindOfMeter;
        this.substitute = substitute;
    }

    public UUID getCustomerUUID() {
        return customerUUID;
    }

    public void setCustomerUUID(UUID customerUUID) {
        this.customerUUID = customerUUID;
    }

    @Override
    public String getMeterId() {
        return meterId;
    }

    @Override
    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    @Override
    public LocalDate getDateOfReading() {
        return dateOfReading;
    }

    @Override
    public void setDateOfReading(LocalDate dateOfReading) {
        this.dateOfReading = dateOfReading;
    }

    @Override
    public Double getMeterCount() {
        return meterCount;
    }

    @Override
    public void setMeterCount(Double meterCount) {
        this.meterCount = meterCount;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public Customer getCustomer() {
        return null;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setCustomer(Customer customer) {

    }

    @Override
    public KindOfMeter getKindOfMeter() {
        return kindOfMeter;
    }

    public void setKindOfMeter(KindOfMeter kindOfMeter) {
        this.kindOfMeter = kindOfMeter;
    }

    @Override
    public Boolean getSubstitute() {
        return substitute;
    }

    @Override
    public String printDateOfReading() {
        return "";
    }

    public void setSubstitute(Boolean substitute) {
        this.substitute = substitute;
    }

    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public void setId(UUID id) {
    }
}
