package ru.fizteh.fivt.students.gudkov394.MultiMap;

import ru.fizteh.fivt.students.gudkov394.map.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

/**
 * Created by kagudkov on 28.09.14.
 */
public class Write {

    public Write(final CurrentTable currentTable, File f) {
    Set<String> set = currentTable.keySet();
    for (String s : set) {
        int hashcode = s.hashCode();
        int ndirectory = hashcode % 16;
        int nfile = hashcode / 16 % 16;

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(new File(f + File.separator +  ((Integer)ndirectory).toString() + ".dir") + File.separator +  ((Integer)nfile).toString() + ".dat");
        } catch (FileNotFoundException e) {
            System.err.println("Input file didn't find");
        }

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
