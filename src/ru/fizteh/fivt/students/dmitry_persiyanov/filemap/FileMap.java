package ru.fizteh.fivt.students.dmitry_persiyanov.filemap;

import ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands.*;

import java.io.*;
import java.util.*;

public final class FileMap {
    private static final String DB_FILE_PATH = System.getProperty("db.file");
    private static RandomAccessFile dbFile = null;
    private static Map<String, String> fileHashMap = null;

    public static Map<String, String> getFileHashMap() {
        return fileHashMap;
    }
    public static RandomAccessFile getDbFile() {
        return dbFile;
    }

    private static void loadHashMap() throws IOException {
        fileHashMap = new HashMap<String, String>();
        if (dbFile.length() != 0) {
            try (ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
                long currentSeek = 0;
                int b = -1;
                LinkedList<String> keys = new LinkedList<>();
                LinkedList<Integer> offsets = new LinkedList<>();
                // Reading keys and offsets.
                while ((b = (int) dbFile.readByte()) != 0) {
                    currentSeek++;
                    buf.write(b);
                }
                currentSeek++;
                keys.add(buf.toString("UTF-8"));
                buf.reset();
                int firstOffset = dbFile.readInt();
                offsets.add(firstOffset);
                currentSeek += 4;
                while (currentSeek != firstOffset) {
                    while ((b = (int) dbFile.readByte()) != 0) {
                        currentSeek++;
                        buf.write(b);
                    }
                    currentSeek++;
                    keys.add(buf.toString("UTF-8"));
                    buf.reset();
                    offsets.add(dbFile.readInt());
                    currentSeek += 4;
                }
                // End of reading keys and offsets.
                // Reading values and filling hashmap.
                Iterator<String> keysIter = keys.iterator();
                Iterator<Integer> offsetIter = offsets.iterator();
                offsetIter.next();
                while (offsetIter.hasNext()) {
                    int nextOffset = offsetIter.next();
                    while (currentSeek != nextOffset) {
                        buf.write(dbFile.readByte());
                        currentSeek++;
                    }
                    fileHashMap.put(keysIter.next(), buf.toString("UTF-8"));
                    buf.reset();
                }
                // Reading last value.
                while (currentSeek != dbFile.length()) {
                    buf.write(dbFile.readByte());
                    currentSeek++;
                }
                fileHashMap.put(keysIter.next(), buf.toString("UTF-8"));
                buf.reset();
            }
        }
    }

    public static void dumpHashMap() throws IOException {
        dbFile.setLength(0);
        Set<String> keys = fileHashMap.keySet();
        LinkedList<Integer> offsetsPlaces = new LinkedList<>();
        for (String key : keys) {
            dbFile.write(key.getBytes("UTF-8"));
            dbFile.write('\0');
            offsetsPlaces.add((int) dbFile.getFilePointer());
            dbFile.writeInt(0);
        }
        Collection<String> values = fileHashMap.values();
        int currentOffset = (int) dbFile.getFilePointer();
        Iterator<Integer> offsetsPlacesIter = offsetsPlaces.iterator();
        for (String val : values) {
            byte[] valBytes = val.getBytes("UTF-8");
            dbFile.write(valBytes);
            int offsetPlace = offsetsPlacesIter.next();
            dbFile.seek(offsetPlace);
            dbFile.writeInt(currentOffset);
            currentOffset += valBytes.length;
            dbFile.seek(currentOffset);
        }
    }

    public static void main(final String[] args) {
        try {
            dbFile = new RandomAccessFile(DB_FILE_PATH, "rw");
            loadHashMap();
            if (args.length == 0) {
                System.out.print("$ ");
                interactiveMode();
            } else {
                Command[] commands = CommandsParser.parse(args);
                batchMode(commands);
            }
        } catch (FileNotFoundException e) {
            System.err.println("trouble with file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("trouble has been occured: " + e.getMessage());
            System.exit(2);
        }

    }

    public static void interactiveMode() throws IOException {
        try (Scanner in = new Scanner(System.in)) {
            while (in.hasNextLine()) {
                try {
                    Command cmd = CommandsParser.parse(in.nextLine());
                    cmd.execute();
                    if (!cmd.getMsg().equals("")) {
                        System.out.println(cmd.getMsg());
                        System.out.print("$ ");
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public static void batchMode(final Command[] commands) throws IOException {
        for (Command cmd : commands) {
            cmd.execute();
            System.out.println(cmd.getMsg());
        }
        dumpHashMap();
    }
}
