package ru.fizteh.fivt.students.AndrewTimokhin.FileMap;

import java.util.*;
import java.io.*;

public class Write {
    private String filepath;

    public Write(String path) {
        filepath = path;
    }

    public void write(Map<String, String> map) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(
                filepath))) {
            Set<String> st = map.keySet();
            for (String time : st) {
                out.writeInt(time.length());
                out.writeChars(time);
                out.writeInt(map.get(time).length());
                out.writeChars(map.get(time));

            }

        } catch (FileNotFoundException e) {
            System.err.print("Not Found " + e.toString());
        } catch (IOException e) {
            System.err.print("IOException " + e.toString());
        }
    }
}
