package com.example.demo.models;

import com.example.demo.interfaces.IReading;

import java.time.LocalDate;
import java.util.UUID;

public class Reading implements IReading {

    // when wir readings ziehen dann tun wir durch customer_uuid ( defined in the database) das Customer initialisieren, but will the rest null or make request?
    private Customer customer;
    private UUID id;
    private String meterId;
    private LocalDate dateOfReading;
    private Double meterCount;
    private String comment;
    private KindOfMeter kindOfMeter;
    private Boolean substitute;

    public Reading(Customer customer, UUID id, String meterId, LocalDate dateOfReading, Double meterCount, String comment, KindOfMeter kindOfMeter, Boolean substitute) {
        this.customer = customer;
        this.id = id;
        this.meterId = meterId;
        this.dateOfReading = dateOfReading;
        this.meterCount = meterCount;
        this.comment = comment;
        this.kindOfMeter = kindOfMeter;
        this.substitute = substitute;
    }

    @Override
    public Customer getCustomer() {
        // todo: call CustomerService to get customer by id ?? and return it instead of the "fake customer" (just customer_id and nulls)
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        // todo: call CustomerService update/patch to get patch/update customer by id ?? and return it instead of the "fake customer" (just customer_id and nulls)
        this.customer = customer;
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

    public void setComment(String comment) {
        this.comment = comment;
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
