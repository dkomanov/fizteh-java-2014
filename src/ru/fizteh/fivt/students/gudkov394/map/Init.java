package ru.fizteh.fivt.students.gudkov394.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class Init {
    public Init(Map currentTable, final String property) {
        if (property == null) {
            System.err.println("You forgot file");
            System.exit(4);
        }
        File f = new File(property);
        FileInputStream input = null;
        try {
            input = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            System.err.println("Input file didn't find");
            System.exit(4);
        }
        try {
            while (input.available() > 0) {
                int length = readInt(input);
                String key = readString(input, length);
                length = readInt(input);
                key = readString(input, length);
                length = readInt(input);
                String value = readString(input, length);
                length = readInt(input);
                value = readString(input, length);
                currentTable.put(key, value);
            }
        } catch (IOException e) {
            System.err.println("failed file");
            System.exit(3);
        }
        try {
            input.close();
        } catch (IOException e) {
            System.err.println("I can't close file");
            System.exit(6);
        }
    }

    private static byte[] read(final FileInputStream input, final int length) throws IOException {
        byte[] buffer = new byte[length];
        input.read(buffer);
        return buffer;
    }

    public static int readInt(final FileInputStream input) throws IOException {
        ByteBuffer wrapped = ByteBuffer.wrap(read(input, 4));
        return wrapped.getInt();
    }

    public static String readString(final FileInputStream input, final int length) throws IOException {
        if (length <= 0) {
            throw new IOException("Length must be positive number");
        }
        byte[] buffer = read(input, length);
        return new String(buffer, "UTF-8");
    }
}
