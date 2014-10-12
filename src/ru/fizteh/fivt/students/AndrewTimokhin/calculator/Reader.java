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
        StringBuilder st = new StringBuilder();
        StringBuilder st2 = new StringBuilder();
        int lenght;
        try (DataInputStream rd = new DataInputStream(new FileInputStream(
                filepath))) {
            while (true) {
                try {
                    lenght = rd.readInt();
                    for (int i = 0; i < lenght; i++) {
                        st.append(rd.readChar());

                    }
                    lenght = rd.readInt();
                    for (int i = 0; i < lenght; i++) {
                        st2.append(rd.readChar());

                    }
                    map.put(st.toString(), st2.toString());
                    st.replace(0, st.length(), "");
                    st2.replace(0, st2.length(), "");

                } catch (EOFException e) {
                    break;
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("Not Found " + e.toString());
        } catch (IOException e) {
            System.out.println("IOException " + e.toString());
        }

        return map;
    }

}
