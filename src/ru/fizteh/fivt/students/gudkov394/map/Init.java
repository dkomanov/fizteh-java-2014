package ru.fizteh.fivt.students.gudkov394.map;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class Init {
    public Init(Map currentTable, final String property) {
        if (property == null) {
            System.err.println("You forgot file");
            System.exit(4);
        }
        File f = new File(property);
        if (!Files.exists(Paths.get(property))) {
            try {
                Files.createFile(Paths.get(property));
            } catch (IOException e) {
                System.err.println("I can't create file");
                System.exit(4);
            }
        }
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            System.err.println("Input file didn't find, I will create it");
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
                if (currentTable.containsKey(key)) {
                    System.err.println("Wrong data: same keys");
                    System.exit(2);
                }
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

    private static byte[] read(final DataInputStream input, final int length) throws IOException {
        if (length < 0) {
            System.err.println("Incorrect data base file: negative length");
            System.exit(2);
        }
        ArrayList<Byte> wordBuilder = new ArrayList<Byte>();
        try {
            for (int i = 0; i < length; i++) {
                byte symbol = input.readByte();
                wordBuilder.add(symbol);
            }
        } catch (EOFException e) {
            System.err.println("unexpected end of file");
            System.exit(2);
        } catch (OutOfMemoryError e) {
            System.err.println("Not enough memory to store database");
            System.exit(2);
        }
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; ++i) {
            buffer[i] = wordBuilder.get(i);
        }
        return buffer;
    }

    public static int readInt(final DataInputStream input) throws IOException {
        ByteBuffer wrapped = ByteBuffer.wrap(read(input, 4));
        return wrapped.getInt();
    }

    public static String readString(final DataInputStream input, final int length) throws IOException {
        if (length <= 0) {
            throw new IOException("Length must be positive number");
        }
        byte[] buffer = read(input, length);
        return new String(buffer, "UTF-8");
    }
}
