package ru.fizteh.fivt.students.gudkov394.map;


import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

public class Exit {
    public Exit(final String[] currentArgs, final Map currentTable) {
        if (currentArgs.length > 1) {
            System.err.println("extra arguments for exit");
            System.exit(1);
        }
        write(currentTable);
        System.exit(0);
    }

    private void write(final Map currentTable) {
        if (System.getProperty("db.file") == null) {
            System.err.println("You forgot file");
            System.exit(4);
        }
        File f = new File(System.getProperty("db.file"));
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            System.err.println("Input file didn't find");
        }
        Set<String> set = currentTable.keySet();
        for (String s : set) {
            writeWord(s, output);
            String tmp = currentTable.get(s).toString();
            writeWord(tmp, output);
        }
    }

    private void writeWord(final String s, final FileOutputStream output) {

        String key = ((Integer) s.length()).toString();
        writeBytes(key, output);
        writeBytes(s, output);
    }

    private void writeBytes(final String key, final FileOutputStream output) {
        try {
            byte[] bytes = ByteBuffer.allocate(4).putInt(key.length()).array();
            output.write(bytes);
        } catch (NullPointerException e) {
            System.err.println("Problem with write");
            System.exit(5);
        } catch (IOException e) {
            System.err.println("Problem with write");
            System.exit(5);
        }
        try {
            output.write(key.getBytes("UTF-8"));
        } catch (IOException e2) {
            System.err.println("Problem with write key");
            System.exit(5);
        }
    }
}
