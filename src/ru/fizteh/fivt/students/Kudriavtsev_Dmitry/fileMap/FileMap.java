package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.fileMap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class FileMap {
    private HashMap<String, String> dBase;
    private String nameOfFile;

    public FileMap(String nameOfFile) {
        this.dBase = new HashMap<>();
        this.nameOfFile = nameOfFile;
    }

    public boolean readFromFile() {
        try {
            FileInputStream iStream = new FileInputStream(nameOfFile);
            FileChannel iChannel = iStream.getChannel();
            ByteBuffer buffer;
            buffer = iChannel.map(FileChannel.MapMode.READ_WRITE, 0, iChannel.size());
            while (buffer.hasRemaining()) {
                int keySize = buffer.getInt();
                byte[] key = new byte[keySize];
                buffer.get(key, 0, keySize);

                int valueSize = buffer.getInt();
                byte[] value = new byte[valueSize];
                buffer.get(value, 0, valueSize);

                dBase.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean writeToFile() {
        try {
            FileOutputStream oStream = new FileOutputStream(nameOfFile);
            Set<String> keySet = dBase.keySet();
            ByteBuffer buffer = ByteBuffer.allocate(4);
            for (String key : keySet) {
                byte[] keyInByte = key.getBytes("UTF-8");
                byte[] valueInByte = dBase.get(key).getBytes("UTF-8");
                oStream.write(buffer.putInt(0, valueInByte.length).array());
                oStream.write(keyInByte);

                oStream.write((buffer.putInt(0, valueInByte.length).array()));
                oStream.write(valueInByte);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean interactiveMode() {
        HashMap<String, Command> myCommands = new HashMap<>();
        myCommands.put("put", new Put());
        myCommands.put("get", new Get());
        myCommands.put("remove", new Remove());
        myCommands.put("list", new List());
        String[] arguments;
        while (true) {
            try {
                System.out.print("$ ");
                Scanner sc = new Scanner(System.in);
                String s1 = sc.nextLine();
                arguments = s1.split("\\s+");
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                System.exit(-1);
                return false;
            }
            if (arguments[0].equals("exit")) {
                break;
            }
            Command whatToDo = myCommands.get(arguments[0]);
            if (whatToDo == null) {
                System.out.println("Not found command: " + arguments[0]);
                System.exit(-1);
                return false;
            }
            String[] newArgs = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, newArgs, 0, newArgs.length);
            if (!whatToDo.exec(dBase, newArgs)) {
                System.out.println("Error on " + whatToDo.name);
                System.exit(-1);
                return false;
            } else {
                if (!writeToFile()) {
                    System.out.println("can't write to file");
                    System.exit(-1);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean pocketMode(String[] args) {
        if (args.length == 0) {
            System.out.println("Bad arguments for pocket mode");
            System.exit(-1);
            return false;
        }
        String merged = null;
        String[] s;
        HashMap<String, Command> myCommands = new HashMap<>();
        myCommands.put("Put", new Put());
        myCommands.put("Get", new Get());
        myCommands.put("Remove", new Remove());
        myCommands.put("List", new List());

        for (String arg : args) {
            merged += arg;
        }
        try {
            s = merged.split(";");
            for (String newCommand :s) {
                String[] arguments = newCommand.split("\\s+");
                if (arguments[0].equals("exit")) {
                    break;
                }
                Command whatToDo = myCommands.get(arguments[0]);
                if (whatToDo == null) {
                    System.out.println("Not found command: " + arguments[0]);
                    System.exit(-1);
                    return false;
                }
                String[] newArgs = new String[arguments.length - 1];
                System.arraycopy(arguments, 1, newArgs, 0, newArgs.length);
                if (!whatToDo.exec(dBase, newArgs)) {
                    System.out.println("Error on " + whatToDo.name);
                    System.exit(-1);
                    return false;
                } else {
                    if (!writeToFile()) {
                        System.out.println("can't write to file");
                        System.exit(-1);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
            return false;
        }
        return true;
    }
}
