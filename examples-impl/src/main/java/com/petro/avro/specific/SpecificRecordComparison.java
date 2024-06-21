package com.petro.avro.specific;

import com.delasport.wallet.Action;
import com.delasport.wallet.ContainerArray;
import com.petro.Container;
import com.petro.DecimalContainer;
import com.petro.Snapshot;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

public class SpecificRecordComparison {

    private static final List<String> BALANCE_IDS = IntStream.range(0, 200)
            .mapToObj(it -> UUID.randomUUID().toString())
            .toList();
    private static final Instant NOW = Instant.now();

//    public static void main(String[] args) {
//
//        final long startMap = System.currentTimeMillis();
//        measureMap();
//        final long endMap = System.currentTimeMillis();
//
//        final long startArray = System.currentTimeMillis();
//        measureArray();
//        final long endArray = System.currentTimeMillis();
//
//        System.out.printf("Map time is %s ms. Array time is %s ms", endMap - startMap, endArray - startArray);
//    }

    public static void main(String[] args) {
        measureDecimal(18, 2, 1000);
    }

    private static void measureMap() {
        final Container container = Container.newBuilder()
                .setSnapshots(BALANCE_IDS.stream()
                        .collect(Collectors.toMap(Function.identity(), it ->
                                Snapshot.newBuilder()
                                        .setAge(Integer.MAX_VALUE)
                                        .setHeight(Float.MAX_VALUE)
                                        .setWeight(Float.MAX_VALUE)
                                        .setFirstName(it)
                                        .setLastName(it)
                                        .setCreatedTs(NOW)
                                        .setUpdatedTs(NOW)
                                        .build())
                        )
                )
                .build();

        // write it out to a file
        final DatumWriter<Container> datumWriter = new SpecificDatumWriter<>(Container.class);

        try (DataFileWriter<Container> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(container.getSchema(), new File("container-map.avro"));
            dataFileWriter.append(container);
            System.out.println("successfully wrote container-map.avro");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void measureArray() {
        final ContainerArray container = ContainerArray.newBuilder()
                .setActions(BALANCE_IDS.stream()
                        .map(it -> Action.newBuilder()
                                .setBalanceId(it)
                                .setAge(Integer.MAX_VALUE)
                                .setHeight(Float.MAX_VALUE)
                                .setWeight(Float.MAX_VALUE)
                                .setFirstName(it)
                                .setLastName(it)
                                .setCreatedTs(NOW)
                                .setUpdatedTs(NOW)
                                .build())
                        .toList()
                )
                .build();

        // write it out to a file
        final DatumWriter<ContainerArray> datumWriter = new SpecificDatumWriter<>(ContainerArray.class);

        try (DataFileWriter<ContainerArray> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(container.getSchema(), new File("container-array.avro"));
            dataFileWriter.append(container);
            System.out.println("successfully wrote container-array.avro");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void measureDecimal(int precision, int scale, int repeat) {
        final String decimal = "1".repeat(precision);

        final String decimalWithScale = scale > 0 ? decimal +  "." + "3".repeat(scale) : decimal;

        final var container = DecimalContainer.newBuilder()
                .setAmount(new BigDecimal(decimalWithScale).stripTrailingZeros())
                .build();

        // write it out to a file
        final DatumWriter<DecimalContainer> datumWriter = new SpecificDatumWriter<>(DecimalContainer.class);

        try (DataFileWriter<DecimalContainer> dataFileWriter = new DataFileWriter<>(datumWriter)) {
            dataFileWriter.create(container.getSchema(), new File("decimal-container.avro"));
            for (int i = 0; i < repeat; i++) {
                dataFileWriter.append(container);
            }
            System.out.println("successfully wrote decimal-container.avro");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
