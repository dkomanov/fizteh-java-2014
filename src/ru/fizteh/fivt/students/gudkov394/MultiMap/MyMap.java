package ru.fizteh.fivt.students.gudkov394.MultiMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class MyMap {
    public Map<String, CurrentTable> tables = null;
    public Boolean checkName(final String name) {
        String[] s = {"put", "get", "remove", "list", "exit", "create", "use", "drop", "show tables"};
        for (int i = 0; i < s.length; ++i) {
            if (name.equals(s[i])) {
                return true;
            }
        }
        return false;
    }

    public void run(final String[] currentArgs, CurrentTable ct) {
        if ("put".equals(currentArgs[0])) {
            Put put = new Put(currentArgs, ct);
        } else if ("get".equals(currentArgs[0])) {
            Get get = new Get(currentArgs, ct);
        } else if ("remove".equals(currentArgs[0])) {
            Remove remove = new Remove(currentArgs, ct);
        } else if ("list".equals(currentArgs[0])) {
            ListTable listTable = new ListTable(currentArgs, ct);
        } else if ("exit".equals(currentArgs[0])) {
            Exit exit = new Exit(currentArgs, ct);
        } else if ("create".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to Create");
                System.exit(1);
            }
            ct = new CurrentTable(currentArgs[1]);
            tables.put(ct.getName(), ct);
        } else if ("use".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to use");
                System.exit(1);
            }
            if(tables.containsKey(currentArgs[1])){
                ct = tables.get(currentArgs[1]);
                System.out.println("using " + ct.getName());
            }
            else{
                System.out.println("tablename not exists");
            }
        }
        else if ("drop".equals(currentArgs[0])) {
            ct.delete();
            tables.remove(ct.getName());
        }else if ("show tables".equals(currentArgs[0])) {
            Set<String> set = tables.keySet();
            System.out.println("table_name row_count");
            for (String s : set) {
                System.out.println(s + " " + ((Integer)tables.get(s).getNumber()).toString());
            }
            System.out.println();
        }
        else {
            System.err.println("wrong command");
            System.exit(22);
        }
    }

    public void interactive() {
        Scanner sc = new Scanner(System.in);
        CurrentTable currentTable = new CurrentTable();
        while (true) {
            String currentString = sc.nextLine();
            currentString = currentString.trim();
                run(currentString.split("\\s+"), currentTable);
        }

    }

    public void packageMode(final String[] args) {
        Scanner sc = new Scanner(System.in);
        CurrentTable currentTable = new CurrentTable();
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
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
            run(s, currentTable);
        }
    }


    public MyMap(final String[] args) {
        if (args.length == 0) {
            interactive();
        } else {
            packageMode(args);
        }
    }
}
