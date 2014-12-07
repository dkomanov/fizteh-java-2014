package ru.fizteh.fivt.students.tonmit.fileMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;


public class FileMap {
    private HashMap<String, String> dBase;
    private String nameOfFile;
    public FileMap(String nameOfFile) {
        this.dBase = new HashMap<>();
        this.nameOfFile = nameOfFile;
        File f = new File(nameOfFile);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("Exception :" + e.getMessage());
            }
        }
    }

    public boolean readFromFile() {
        try {
            FileInputStream iStream = new FileInputStream(nameOfFile);
            FileChannel iChannel = iStream.getChannel();
            ByteBuffer buffer;
            String key;
            String value;
            buffer = iChannel.map(FileChannel.MapMode.READ_ONLY, 0, iChannel.size());
            while (buffer.hasRemaining()) {
                byte[] word = new byte[buffer.getInt()];
                buffer.get(word, 0, word.length);
                key = new String(word, "UTF-8");
                word = new byte[buffer.getInt()];
                buffer.get(word, 0, word.length);
                value = new String(word, "UTF-8");
                dBase.put(key, value);
            }
            iStream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean writeToFile() {
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
            oStream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean interactiveMode() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("$ ");
                String s1 = sc.nextLine();
                s1 = s1.trim();
                if (s1.startsWith("'") && s1.endsWith("'")) {
                    s1 = s1.replaceAll("'", "");
                    StringBuffer argStr = new StringBuffer(s1);
                    Scanner mainScanner = new Scanner(argStr.toString());
                    mainScanner.useDelimiter("[ ]*;[ ]*");
                    while (mainScanner.hasNext()) {
                        String str = mainScanner.next();
                        if (!execProc(getArgsFromString(str))) {
                            return false;
                        }
                    }
                } else {
                    String[] args = s1.split("\\s+");
                    if (args[0].equals("put")) {
                        if (!put(args)) {
                            System.out.println("Error on put");
                            System.exit(-1);
                            sc.close();
                            return false;
                        }

                    } else if (args[0].equals("get")) {
                        if (!get(args)) {
                            System.out.println("Error on get");
                            System.exit(-1);
                            sc.close();
                            return false;
                        }

                    } else if (args[0].equals("list")) {
                        if (!list(args)) {
                            System.out.println("Error on list");
                            System.exit(-1);
                            sc.close();
                            return false;
                        }

                    } else if (args[0].equals("remove")) {
                        if (!remove(args)) {
                            System.out.println("Error on remove");
                            System.exit(-1);
                            sc.close();
                            return false;
                        }

                    } else if (args[0].equals("exit")) {
                        sc.close();
                        System.exit(-1);

                    } else {
                        System.out.println("Not found command: " + args[0]);
                        System.exit(-1);
                        sc.close();
                        return false;
                    }
                    if (!writeToFile()) {
                        System.out.println("can't write to file");
                        System.exit(-1);
                        sc.close();
                        return false;
                    }
                }
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                System.exit(-1);
                sc.close();
                return false;
            }
            /*

            */
        }
    }

    private String[] getArgsFromString(String str) {
        str = str.trim();
        str = str.replaceAll("[ ]+", " ");
        int countArgs = 1;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == ' ') {
                ++countArgs;
            }
        }
        if (!str.isEmpty()) {
            Scanner stringScanner = new Scanner(str);
            stringScanner.useDelimiter(" ");
            String[] cmdArgs = new String[countArgs];
            for (int i = 0; stringScanner.hasNext(); ++i) {
                cmdArgs[i] = stringScanner.next();
            }
            stringScanner.close();
            return cmdArgs;
        }
        return null;
    }

    public boolean pocketMode(String[] args) {
        StringBuffer argStr = new StringBuffer(args[0]);
        for (int i = 1; i < args.length; ++i) {
            argStr.append(" ");
            argStr.append(args[i]);
        }
        Scanner mainScanner = new Scanner(argStr.toString());
        mainScanner.useDelimiter("[ ]*;[ ]*");
        while (mainScanner.hasNext()) {
            String str = mainScanner.next();
            return (execProc(getArgsFromString(str)));
        }
        mainScanner.close();
        return true;
    }

    private boolean execProc(String[] args) {
        if (args[0].equals("put")) {
            if (!put(args)) {
                System.out.println("Error on put");
                System.exit(-1);
                return false;
            }

        } else if (args[0].equals("get")) {
            if (!get(args)) {
                System.out.println("Error on get");
                System.exit(-1);
                return false;
            }

        } else if (args[0].equals("list")) {
            if (!list(args)) {
                System.out.println("Error on list");
                System.exit(-1);
                return false;
            }

        } else if (args[0].equals("remove")) {
            if (!remove(args)) {
                System.out.println("Error on remove");
                System.exit(-1);
                return false;
            }

        } else if (args[0].equals("exit")) {
            System.exit(-1);

        } else {
            System.out.println("Not found command: " + args[0]);
            System.exit(-1);
            return false;
        }
        if (!writeToFile()) {
            System.out.println("can't write to file");
            System.exit(-1);
            return false;
        }
        return true;
    }

    private boolean put(String[] args) {
        if (args.length != 3) {
            System.err.println("Incorrect number of arguments in put");
            return false;
        }
        String value = dBase.put(args[1], args[2]);
        if (value != null) {
            System.out.println("Overwrite:\n" + value);
        } else {
            System.out.println("new");
        }
        return true;
    }
    private boolean get(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments in get");
            return false;
        }
        String value = dBase.get(args[1]);
        if (value != null) {
            System.out.println("found:\n" + value);
        } else {
            System.out.println("not found");
        }
        return true;
    }
    private boolean remove(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments in args");
            return false;
        }
        if (dBase.remove(args[1]) != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }
    private boolean list(String[] args) {
        if (args.length != 1) {
            System.err.println("Incorrect number of arguments in list");
            return false;
        }
        Set<String> keySet = dBase.keySet();
        for (String key : keySet) {
            System.out.print(key + " , ");
        }
        System.out.println();
        return true;
    }
}
