package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Set;
import java.util.Scanner;

public class FileMap {
    private HashMap<String, String> dataBase;
    private HashMap<String, Command> fileMapCommands;
    private String diskFile;
    public FileMap(String newDiskFile) {
        dataBase = new HashMap<>();
        fileMapCommands = new HashMap<>();
        diskFile = newDiskFile;
    }

    public void addCommand(Command newCommand) {
        fileMapCommands.put(newCommand.toString(), newCommand);
    }

    public boolean init() {
        FileChannel inputChannel;
        try {
            inputChannel = new FileInputStream(diskFile).getChannel();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return false;
        }
        ByteBuffer bufferFromDisk;
        try {
            bufferFromDisk =
                    inputChannel.map(MapMode.READ_ONLY, 0, inputChannel.size());
        } catch (IOException e) {
            System.out.println("io exception");
            return false;
        }
        try {
            while (bufferFromDisk.remaining() > 0) {
                byte[] key;
                int keySize = bufferFromDisk.getInt();
                key = new byte[keySize];
                bufferFromDisk.get(key, 0, key.length);

                byte[] value;
                int valueSize = bufferFromDisk.getInt();
                value = new byte[valueSize];
                bufferFromDisk.get(value, 0, value.length);
                try {
                    dataBase.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    System.out.println("unsupported encoding");
                    return false;
                }
            }
        } catch (NullPointerException e) {
            System.out.println("null pointer exception");
        }
        return true;
    }

    public boolean load() {
        try (FileOutputStream outputStream = new FileOutputStream(diskFile)) {
            Set<String> keySet = dataBase.keySet();
            ByteBuffer bufferForSize = ByteBuffer.allocate(4);
            for (String key: keySet) {
                try {
                    byte[] keyByte = key.getBytes("UTF-8");
                    byte[] valueByte = dataBase.get(key).getBytes("UTF-8");
                    outputStream.write(bufferForSize.putInt(0, keyByte.length).array());
                    outputStream.write(keyByte);
                    outputStream.write(bufferForSize.putInt(0, valueByte.length).array());
                    outputStream.write(valueByte);
                } catch (UnsupportedEncodingException e) {
                    System.out.println("unsupported encoding");
                    return false;
                } catch (IOException e) {
                    System.out.println("io exception");
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return false;
        } catch (IOException e) {
            System.out.println("io exception");
            return false;
        }
        return true;
    }

    public boolean interactiveMode() {
        boolean ended = false;
        boolean errorOccuried = false;
        try (Scanner inStream = new Scanner(System.in)) {
            String[] parsedCommands;
            String[] parsedArguments;
            System.out.print("$ ");
            while (!ended) {
                if (inStream.hasNextLine()) {
                    parsedCommands = inStream.nextLine().split(";|\n");
                } else {
                    continue;
                }
                for (String oneCommand : parsedCommands) {
                    parsedArguments = oneCommand.split("\\s+");
                    if (parsedArguments[0].equals("exit")) {
                        ended = true;
                        break;
                    }
                    Command commandToExecute = fileMapCommands.get(parsedArguments[0]);
                    if (commandToExecute != null) {
                        if (!commandToExecute.run(dataBase, parsedArguments)) {
                            errorOccuried = true;
                        } else {
                            load();
                        }
                    } else {
                        System.out.println(parsedArguments[0] + ": command not found");
                        errorOccuried = true;
                    }
                }
                if (!ended) {
                    System.out.print("$ ");
                }
            }
        }
        return !errorOccuried;
    }

    public boolean packetMode(String[] arguments) {

        String[] parsedCommands;
        String[] parsedArguments;
        String commandLine = arguments[0];
        boolean errorOccuried = false;

        for (int i = 1; i < arguments.length; ++i) {
            commandLine = commandLine + " " + arguments[i];
        }

        parsedCommands = commandLine.split(";|\n");
        for (String oneCommand : parsedCommands) {
            parsedArguments = oneCommand.trim().split("\\s+");
            if (parsedArguments[0].equals("exit")) {
                return true;
            }
            Command commandToExecute = fileMapCommands.get(parsedArguments[0]);
            if (commandToExecute != null) {
                if (!commandToExecute.run(dataBase, parsedArguments)) {
                    errorOccuried = true;
                } else {
                    load();
                }
            } else {
                System.out.println(parsedArguments[0] + ": command not found");
                errorOccuried = true;
            }
        }
        return !errorOccuried;
    }
}
