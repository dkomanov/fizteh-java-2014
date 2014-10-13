package ru.fizteh.fivt.students.YaronskayaLiubov.FileMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by luba_yaronskaya on 06.10.14.
 */
public class FileMap {
    public static File curDB;
    protected static HashMap<String, String> data;
    public static boolean errorOccurred;

    private static HashMap<String, Command> fileMapCommands;

    public static boolean exec(String[] args) {
        String curDBPath = System.getProperty("db.file");
        errorOccurred = false;
        if (curDBPath == null) {
            System.err.println("database not found");
            return false;
        }
        curDB = new File(curDBPath);
        fileMapCommands = new HashMap<String, Command>();
        data = new HashMap<String, String>();
        fileMapCommands.put("exit", new ExitCommand());
        fileMapCommands.put("get", new GetCommand());
        fileMapCommands.put("list", new ListCommand());
        fileMapCommands.put("put", new PutCommand());
        fileMapCommands.put("remove", new RemoveCommand());
        FileMap.loadDBData();
        if (args.length == 0) {
            while (true) {
                System.out.print(System.getProperty("user.dir") + "$ ");
                Scanner scanner = new Scanner(System.in);
                if (!scanner.hasNextLine()) {
                    break;
                }
                for (String s : scanner.nextLine().split(";")) {
                    String[] argv = s.trim().split("\\s+");
                    String curCommand = argv[0];
                    if (curCommand.equals("")) {
                        //System.out.println();
                        //System.out.print(System.getProperty("user.dir") + "$ ");
                        continue;
                    }
                    if (fileMapCommands.get(curCommand) == null) {
                        System.out.println(curCommand
                                + ": command not found");
                        errorOccurred = true;
                        continue;
                    }
                    if (!fileMapCommands.get(curCommand).execute(
                            argv)) {errorOccurred = true;}
                }
            }
        } else {
            StringBuilder joinedArgs = new StringBuilder();
            for (String s : args) {
                joinedArgs.append(s);
                joinedArgs.append(" ");
            }
            String[] argsPool = joinedArgs.toString().split(";");
            for (String s : argsPool) {
                String[] argv = s.trim().split("\\s+");
                String curCommand = argv[0];
                if (curCommand.equals("")) {
                    continue;
                }
                if (fileMapCommands.get(curCommand) == null) {
                    System.out.println(curCommand
                            + ": command not found");
                    errorOccurred = true;
                    continue;
                }
                if (!fileMapCommands.get(curCommand).execute(
                        argv)) {
                    errorOccurred = true;
                }
            }

        }

        try {
            FileMap.save();
        } catch (IOException e) {
            System.err.println("Error writing file");
        }

        return !errorOccurred;
    }

    public static void loadDBData() {
        FileChannel channel = null;
        try {
            channel = new FileInputStream(curDB.getCanonicalPath()).getChannel();

            ByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

            while (byteBuffer.remaining() > 0) {
                int keyLength = byteBuffer.getInt();
                byte[] key = new byte[keyLength];
                byteBuffer.get(key, 0, keyLength);
                int valueLength = byteBuffer.getInt();
                byte[] value = new byte[valueLength];

                byteBuffer.get(value, 0, valueLength);
                data.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
            }
        } catch (IOException e) {
            System.err.println("error reading file" + e.toString()
            );
        }
    }

    public static void save() throws IOException {
        FileOutputStream fos = new FileOutputStream(curDB);
        for (String key : data.keySet()) {
            String value = data.get(key);
            byte[] keyInBytes = new byte[0];
            byte[] valueInBytes = new byte[0];
            try {
                keyInBytes = key.getBytes("UTF-8");
                valueInBytes = value.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println("Wrong encoding");
            }
            ByteBuffer bb = ByteBuffer.allocate(8 + keyInBytes.length + valueInBytes.length);
            bb.putInt(keyInBytes.length);
            bb.put(keyInBytes);
            bb.putInt(valueInBytes.length);
            bb.put(valueInBytes);
            int limit = bb.limit();
            for (int i = 0; i < limit; ++i) {
                fos.write(bb.get(i));
            }
        }
        fos.close();
    }

    public static void main(String[] args) {
        boolean errorOccurred;
        try {
            errorOccurred = !FileMap.exec(args);
        } catch (Exception e) {
            System.err.println(e.toString());
            errorOccurred = true;
        }
        if (errorOccurred) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
