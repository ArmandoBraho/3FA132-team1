package com.example.demo.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReadingQueryBuilder {
    private final StringBuilder query = new StringBuilder();
    private final List<Object> params = new ArrayList<>();
    private boolean whereAdded = false;

    public ReadingQueryBuilder() {
        query.append("SELECT * FROM readings");
    }

    public ReadingQueryBuilder withIds(List<UUID> ids) {
        if (!ids.isEmpty()) {
            addCondition("id IN (" + String.join(",", Collections.nCopies(ids.size(), "?")) + ")");
            params.addAll(ids.stream()
                    .map(UUID::toString)
                    .toList());
        }
        return this;
    }

    public ReadingQueryBuilder withCustomer(UUID customerId) {
        if (customerId != null) {
            addCondition("customer_id = ?");
            params.add(customerId.toString());
        }
        return this;
    }

    public ReadingQueryBuilder withDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null) {
            addCondition("date_of_reading >= ?");
            params.add(startDate.toString());
        }

        if (endDate != null) {
            addCondition("date_of_reading <= ?");
            params.add(endDate.toString());
        }
        return this;
    }

    public ReadingQueryBuilder withKindOfMeter(String kindOfMeter) {
        if (kindOfMeter != null) {
            addCondition("kind_of_meter = ?");
            params.add(kindOfMeter);
        }
        return this;
    }

    private void addCondition(String condition) {
        if (!whereAdded) {
            query.append(" WHERE ");
            whereAdded = true;
        } else {
            query.append(" AND ");
        }
        query.append(condition);
    }

    public String getQuery() {
        return query.toString();
    }

    public List<Object> getParams() {
        return params;
    }
}

// Usage:
//ReadingQueryBuilder builder = new ReadingQueryBuilder()
//        .withIds(ids)
//        .withCustomer(customerId)
//// ... add other conditions ...
//String query = builder.getQuery();