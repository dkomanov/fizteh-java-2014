package ru.fizteh.fivt.students.dmitry_persiyanov.filemap;

import ru.fizteh.fivt.students.dmitry_persiyanov.filemap.commands.*;

import java.io.*;
import java.util.*;

class CommandsParser {
    public static Command[] parse(final String[] args) {
        StringBuilder buf = new StringBuilder();
        for (String arg : args) {
            buf.append(arg);
            buf.append(" ");
        }
        String[] str_commands = buf.toString().trim().split(";");
        Command[] commands = new Command[str_commands.length];
        for (int i = 0; i < str_commands.length; ++i) {
            commands[i] = parseCommand(str_commands[i]);
        }
        return commands;
    }

    public static Command parse(final String arg) {
        String[] args = new String[1];
        args[0] = arg;
        Command[] cmds = parse(args);
        return cmds[0];
    }

    private static Command parseCommand(final String str_command) {
        String[] args = str_command.trim().split("\\s+");
        switch (args[0]) {
            case "put":
                return new PutCommand(args);
            case "get":
                return new GetCommand(args);
            case "remove":
                return new RemoveCommand(args);
            case "list":
                return new ListCommand(args);
            case "exit":
                return new ExitCommand(args);
            default:
                throw new IllegalArgumentException("wrong command: " + str_command);
        }
    }
}

public final class FileMap {
    private static final String DbFilePath = System.getProperty("db.file");
    private static RandomAccessFile DbFile = null;
    private static Map<String, String> FileHashMap = null;

    public static Map<String, String> getFileHashMap() { return FileHashMap; }
    public static RandomAccessFile getDbFile() { return DbFile; }

    private static void fillFileHashMap() throws IOException {
        FileHashMap = new HashMap<String, String>();
        if (DbFile.length() != 0) {
            try (ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
                long currentSeek = 0;
                int b = -1;
                LinkedList<String> keys = new LinkedList<>();
                LinkedList<Integer> offsets = new LinkedList<>();
                // reading keys and offsets
                while ((b = (int) DbFile.readByte()) != 0) {
                    currentSeek++;
                    buf.write(b);
                }
                keys.add(buf.toString("UTF-8"));
                buf.reset();
                int firstOffset = DbFile.readInt();
                offsets.add(firstOffset);
                currentSeek += 4;
                while (currentSeek != firstOffset) {
                    while ((b = (int) DbFile.readByte()) != 0) {
                        currentSeek++;
                        buf.write(b);
                    }
                    keys.add(buf.toString("UTF-8"));
                    buf.reset();
                    offsets.add(DbFile.readInt());
                    currentSeek += 4;
                }
                // end of reading keys and offsets
                // reading values and filling hashmap
                Iterator<String> keysIter = keys.iterator();
                Iterator<Integer> offsetIter = offsets.iterator();
                offsetIter.next();
                while (offsetIter.hasNext()) {
                    int nextOffset = offsetIter.next();
                    while (currentSeek != nextOffset) {
                        buf.write(DbFile.readByte());
                        currentSeek++;
                    }
                    FileHashMap.put(keysIter.next(), buf.toString("UTF-8"));
                    buf.reset();
                }
                // reading last value
                while (currentSeek != DbFile.length()) {
                    buf.write(DbFile.readByte());
                    currentSeek++;
                }
                FileHashMap.put(keysIter.next(), buf.toString("UTF-8"));
                buf.reset();
            }
        }
    }

    public static void saveChangesToFile() throws IOException {
        DbFile.setLength(0);
        Set<String> keys = FileHashMap.keySet();
        LinkedList<Integer> offsetsPlaces = new LinkedList<>();
        for (String key : keys) {
            DbFile.write(key.getBytes("UTF-8"));
            DbFile.write('\0');
            offsetsPlaces.add((int) DbFile.getFilePointer());
            DbFile.writeInt(0);
        }
        Collection<String> values = FileHashMap.values();
        int currentOffset = (int) DbFile.getFilePointer();
        Iterator<Integer> offsetsPlacesIter = offsetsPlaces.iterator();
        for (String val : values) {
            byte[] valBytes = val.getBytes("UTF-8");
            DbFile.write(valBytes);
            int offsetPlace = offsetsPlacesIter.next();
            DbFile.seek(offsetPlace);
            DbFile.writeInt(currentOffset);
            currentOffset += valBytes.length;
            DbFile.seek(currentOffset);
        }
    }

    public static void main(final String[] args) {
        try {
            DbFile = new RandomAccessFile(DbFilePath, "rw");
            fillFileHashMap();
            if (args.length == 0) {
                interactiveMode();
            } else {
                Command[] commands = CommandsParser.parse(args);
                packetMode(commands);
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
                Command cmd = CommandsParser.parse(in.nextLine());
                cmd.execute();
            }
        }
    }

    public static void packetMode(final Command[] commands) throws IOException {
        for (Command cmd : commands) {
            cmd.execute();
        }
    }
}
