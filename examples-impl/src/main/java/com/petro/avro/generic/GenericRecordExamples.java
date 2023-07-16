package com.petro.avro.generic;

import java.io.File;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;

public class GenericRecordExamples {

  public static void main(String[] args) {
    final var parser = new Schema.Parser();

    final var schema = parser.parse("""
        {
             "type": "record",
             "namespace": "com.example",
             "name": "Customer",
             "doc": "Avro Schema for our Customer",
             "fields": [
               { "name": "first_name", "type": "string", "doc": "First Name of Customer" },
               { "name": "last_name", "type": "string", "doc": "Last Name of Customer" },
               { "name": "age", "type": "int", "doc": "Age at the time of registration" },
               { "name": "height", "type": "float", "doc": "Height at the time of registration in cm" },
               { "name": "weight", "type": "float", "doc": "Weight at the time of registration in kg" },
               { "name": "automated_email", "type": "boolean", "default": true, "doc": "Field indicating if the user is enrolled in marketing emails" }
             ]
        }
                """);

    final GenericRecordBuilder genericRecordBuilder = new GenericRecordBuilder(schema);

    genericRecordBuilder.set("first_name", "John");
    genericRecordBuilder.set("last_name", "Doe");
    genericRecordBuilder.set("age", 26);
    genericRecordBuilder.set("height", 190f);
    genericRecordBuilder.set("weight", 70.5f);
    genericRecordBuilder.set("automated_email", false);

    final GenericData.Record customer = genericRecordBuilder.build();

    System.out.println(customer);

    // writing to a file
    final DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
    try (DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter)) {
      dataFileWriter.create(customer.getSchema(), new File("customer-generic.avro"));
      dataFileWriter.append(customer);
      System.out.println("Written customer-generic.avro");
    } catch (IOException e) {
      System.out.println("Couldn't write file");
      e.printStackTrace();
    }

    // reading from a file
    final File file = new File("customer-generic.avro");
    final DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
    GenericRecord customerRead;
    try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(file, datumReader)) {
      customerRead = dataFileReader.next();
      System.out.println("Successfully read avro file");
      System.out.println(customerRead.toString());

      // get the data from the generic record
      System.out.println("First name: " + customerRead.get("first_name"));

      // read a non-existent field
//      System.out.println("Non existent field: " + customerRead.get("not_here"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
