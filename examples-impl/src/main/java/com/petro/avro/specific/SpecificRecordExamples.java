package com.petro.avro.specific;

import com.example.Customer;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

public class SpecificRecordExamples {

  public static void main(String[] args) {
    Customer.Builder customerBuilder = Customer.newBuilder();
    customerBuilder.setAge(30);
    customerBuilder.setFirstName("Mark");
    customerBuilder.setLastName("Simpson");
    customerBuilder.setAutomatedEmail(true);
    customerBuilder.setHeight(180f);
    customerBuilder.setWeight(90f);
    customerBuilder.setTimestamp(Instant.now());

    Customer customer = customerBuilder.build();
    System.out.println(customer.toString());


    // write it out to a file
    final DatumWriter<Customer> datumWriter = new SpecificDatumWriter<>(Customer.class);

    try (DataFileWriter<Customer> dataFileWriter = new DataFileWriter<>(datumWriter)) {
      dataFileWriter.create(customer.getSchema(), new File("customer-specific.avro"));
      dataFileWriter.append(customer);
      System.out.println("successfully wrote customer-specific.avro");
    } catch (IOException e){
      e.printStackTrace();
    }


    // read it from a file
    final File file = new File("customer-specific.avro");
    final DatumReader<Customer> datumReader = new SpecificDatumReader<>(Customer.class);
    final DataFileReader<Customer> dataFileReader;
    try {
      System.out.println("Reading our specific record");
      dataFileReader = new DataFileReader<>(file, datumReader);
      while (dataFileReader.hasNext()) {
        Customer readCustomer = dataFileReader.next();
        System.out.println(readCustomer.toString());
        System.out.println("First name: " + readCustomer.getFirstName());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


    final File fileGeneric = new File("customer-generic.avro");
    final DatumReader<Customer> datumReaderGeneric = new SpecificDatumReader<>(Customer.class);
    final DataFileReader<Customer> dataFileReaderGeneric;
    try {
      System.out.println("Reading our specific record from generic");
      dataFileReaderGeneric = new DataFileReader<>(fileGeneric, datumReaderGeneric);
      while (dataFileReaderGeneric.hasNext()) {
        Customer readCustomer = dataFileReaderGeneric.next();
        System.out.println(readCustomer.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
