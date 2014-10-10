package ru.fizteh.fivt.students.gudkov394.MultiMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.io.File;

public class MyMap {
    public Map<String, CurrentTable> tables = new HashMap<String, CurrentTable>();
    public CurrentTable ct = new CurrentTable();

    public Boolean checkName(final String name) {
        String[] s = {"put", "get", "remove", "list", "exit", "create", "use", "drop", "#*#", "size"};
        for (int i = 0; i < s.length; ++i) {
            if (name.equals(s[i])) {
                return true;
            }
        }
        return false;
    }

    private void initMap() {
        File f = new File(System.getProperty("db.file"));
        String[] s = f.list();
        if (s != null) {
            for (String tmp : s) {
                CurrentTable ct = new CurrentTable(tmp);
                ct.init(); //в number запишем количество аргументов, а дальше поддерживаем его
                ct.clear();
                tables.put(tmp, ct);
            }
        }
    }

    boolean obvious() {
        if (ct.getName() == null) {
            System.err.println("no table");
            return false;
        }
        return true;
    }

    public void run(final String[] currentArgs) {
        if ("put".equals(currentArgs[0])) {
            if (obvious()) {
                Put put = new Put(currentArgs, ct);
            }
        } else if ("get".equals(currentArgs[0])) {
            if (obvious()) {
                Get get = new Get(currentArgs, ct);
            }
        } else if ("remove".equals(currentArgs[0])) {
            if (obvious()) {
                if (currentArgs.length != 2) {
                    System.err.println("wrong number of argument to Create");
                    System.exit(1);
                }
                ct.remove(currentArgs[1]);
            }
        } else if ("list".equals(currentArgs[0])) {
            if (obvious()) {
                ListTable listTable = new ListTable(currentArgs, ct);
            }
        } else if ("exit".equals(currentArgs[0])) {
            Exit exit = new Exit(currentArgs);
        } else if ("create".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to Create");
                System.exit(1);
            }
            if (tables.containsKey(currentArgs[1])) {
                System.out.println("tablename exists");
            } else {
                tables.put(currentArgs[1], new CurrentTable(currentArgs[1]));
            }
        } else if ("use".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to use");
                System.exit(1);
            }
            if (tables.containsKey(currentArgs[1])) {
                ct.clear();
                ct = tables.get(currentArgs[1]);
                ct.init();
                System.out.println("using " + ct.getName());
            } else {
                System.out.println("tablename not exists");
            }
        } else if ("drop".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to drop");
                System.exit(1);
            }
            if (tables.containsKey(currentArgs[1])) {
                tables.get(currentArgs[1]).delete();
                tables.remove(currentArgs[1]);
            } else {
                System.out.println("tablename not exists");
            }
        } else if ("#*#".equals(currentArgs[0])) {
            Set<String> set = tables.keySet();
            System.out.println("table_name row_count");
            for (String s : set) {
                System.out.println(s + " " + ((Integer) tables.get(s).size()).toString());
            }
            System.out.println();
        } else if ("size".equals(currentArgs[0])) {
            if (currentArgs.length != 1) {
                System.err.println("wrong number of argument to drop");
                System.exit(1);
            }
            int size = 0;
            for (String s : tables.keySet()) {
                size += tables.get(s).size();
            }
            System.out.println(size);
        } else {
            System.err.println("wrong command");
            System.exit(22);
        }
    }

    public void interactive() {
        Scanner sc = new Scanner(System.in);
        CurrentTable currentTable = new CurrentTable();
        initMap();
        while (true) {

            String currentString = sc.nextLine();
            currentString = currentString.trim();
            currentString = currentString.replaceAll("\\s*;\\s*", ";");
            currentString = currentString.replaceAll("\\s+", " ");
            currentString = currentString.replaceAll("show tables", "#*#");
            run(currentString.split("\\s+"));
        }

    }

    public void packageMode(final String[] args) {
        Scanner sc = new Scanner(System.in);
        CurrentTable currentTable = new CurrentTable();
        initMap();
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
        string = string.replaceAll("\\s*;\\s*", ";");
        string = string.replaceAll("\\s+", " ");
        string = string.replaceAll("show tables", "#*#");
        String[] commands = string.split(";|(\\s+)");
        int i = 0;
        while (i < commands.length) {
            int first = i;
            ++i;
            while (i < commands.length && !checkName(commands[i])) {
                ++i;
            }
            int size = 0;
            for (int j = 0; j < i - first; ++j) {
                if (commands[j + first].length() != 0) {
                    ++size;
                }
            }

            String[] s = new String[size];
            int tmpSize = 0;
            for (int j = 0; j < s.length; ++j) {
                if (commands[j + first].length() != 0) {
                    s[tmpSize] = commands[j + first];
                    ++tmpSize;
                }
            }
            run(s);
        }
        System.exit(0);
    }


    public MyMap(final String[] args) {
        if (args.length == 0) {
            interactive();
        } else {
            packageMode(args);
        }
    }
}
