package ru.fizteh.fivt.students.AndrewTimokhin.FileMap;

import java.util.*;
import java.io.*;

public class Reader {
    private String filepath;
    private HashMap<String, String> map;

    public Reader(String path) {
        filepath = path;
        map = new HashMap<String, String>();
    }

    public HashMap<String, String> read() {
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        int lenght = 0;
        try (DataInputStream rd = new DataInputStream(new FileInputStream(
                filepath))) {
            while (true) {
                try {
                    lenght = rd.readInt();
                    for (int i = 0; i < lenght; i++) {
                        keyBuilder.append(rd.readChar());

                    }
                    lenght = rd.readInt();
                    for (int i = 0; i < lenght; i++) {
                        valueBuilder.append(rd.readChar());

                    }
                    map.put(keyBuilder.toString(), valueBuilder.toString());
                    keyBuilder.replace(0, keyBuilder.length(), "");
                    valueBuilder.replace(0, valueBuilder.length(), "");

                } catch (EOFException e) {
                    break;
                }

            }

        } catch (FileNotFoundException e) {
            try {
                File newdb = new File(filepath);
                newdb.createNewFile();
            } catch (IOException err) {
                System.err.print(err.toString());
            }
        } catch (IOException e) {
            System.err.print(e.toString());
        }

        return map;
    }

}
