package org.gestern.gringotts;

import org.gestern.gringotts.data.UUIDFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UtilTest {
    public static void main(String[] args) throws Exception {
        List<String> names = new ArrayList<>();

        names.add("nikosgram");
        names.add("mallor");

        UUIDFetcher uuidFetcher = new UUIDFetcher(names, true);

        for (UUID value : uuidFetcher.call().values()) {
            System.out.println(value.toString());
        }
    }
}
