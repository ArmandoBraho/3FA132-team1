package com.example.demo.interfaces;

import com.example.demo.models.Customer;

import java.time.LocalDate;

public interface IReading extends IId {

   enum KindOfMeter {
      HEATING, ELECTRICITY, UNKNOWN, WATER
   }

   String getComment();

   Customer getCustomer();

   LocalDate getDateOfReading();

   KindOfMeter getKindOfMeter();

   Double getMeterCount();

   String getMeterId();

   Boolean getSubstitute();

   String printDateOfReading();

   void setComment(String comment);

   void setCustomer(Customer customer);

   void setDateOfReading(LocalDate dateOfReading);

   void setKindOfMeter(KindOfMeter kindOfMeter);

   void setMeterCount(Double meterCount);

   void setMeterId(String meterId);

   void setSubstitute(Boolean substitute);

}
