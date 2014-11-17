package ru.fizteh.fivt.students.alexpodkin.FileMap;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class Launcher {

    private HashMap<String, String> fileMap;
    private Writer writer;
    private boolean exitFlag;
    private PrintStream printStream = System.out;

    public Launcher(HashMap<String, String> file, Writer userWriter) {
        fileMap = file;
        writer = userWriter;
    }

    public boolean launch(String[] arguments) throws IOException {
        if (arguments.length == 0) {
            return exec(System.in, false);
        } else {
            StringBuilder builder = new StringBuilder();
            for (String argument : arguments) {
                builder.append(argument);
                builder.append(" ");
            }
            String request = builder.toString().replaceAll(";", "\n");
            InputStream inputStream = new ByteArrayInputStream(request.getBytes("UTF-8"));
            return exec(inputStream, true);
        }
    }

    private boolean exec(InputStream inputStream, boolean isPackage) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        while (true) {
            String request = bufferedReader.readLine();
            if (request == null) {
                writer.writeDataToFile(fileMap);
                break;
            }

            String[] arguments = request.split(" ");
            if (arguments.length == 0 || arguments[0].equals("")) {
                continue;
            }

            boolean commandCheck;

            switch (arguments[0]) {
                case "put":
                    commandCheck = putCommand(arguments);
                    break;
                case "get":
                    commandCheck = getCommand(arguments);
                    break;
                case "list":
                    commandCheck = listCommand(arguments);
                    break;
                case "remove":
                    commandCheck = removeCommand(arguments);
                    break;
                default:
                    commandCheck = exitCommand(arguments);
            }
            writer.writeDataToFile(fileMap);

            if (exitFlag) {
                return true;
            }

            if (!commandCheck && isPackage) {
                exitFlag = true;
                return false;
            }
        }
        exitFlag = true;
        return true;
    }

    private boolean putCommand(String[] arguments) {
        if (arguments.length != 3) {
            return false;
        }
        if (fileMap.containsKey(arguments[1])) {
            printStream.print("overwrite\n" + fileMap.get(arguments[1]) + "\n");
            fileMap.remove(arguments[1]);
        } else {
            printStream.print("new\n");
        }
        fileMap.put(arguments[1], arguments[2]);
        return true;
    }

    private boolean getCommand(String[] arguments) {
        if (arguments.length != 2) {
            return false;
        }
        if (fileMap.containsKey(arguments[1])) {
            printStream.print("found\n" + fileMap.get(arguments[1]) + "\n");
        } else {
            printStream.print("not found\n");
        }
        return true;
    }

    private boolean removeCommand(String[] arguments) {
        if (arguments.length != 2) {
            return false;
        }
        if (fileMap.containsKey(arguments[1])) {
            fileMap.remove(arguments[1]);
            printStream.print("removed\n");
        } else {
            printStream.print("not found\n");
        }
        return true;
    }

    private boolean listCommand(String[] arguments) {
        if (arguments.length != 1) {
            return false;
        }
        Set<String> keySet = fileMap.keySet();
        if (keySet.isEmpty()) {
            printStream.print("\n");
        } else {
            for (String key : keySet) {
                printStream.print(key + " ");
            }
            printStream.print("\n");
        }
        return true;
    }

    private boolean exitCommand(String[] arguments) {
        if (arguments.length != 1) {
            return false;
        }
        exitFlag = true;
        return true;
    }

}
