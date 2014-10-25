package ru.fizteh.fivt.students.alina_chupakhina.multyfilehashmap;

import java.io.*;
import java.util.*;

public class MultiFileHashMap {

    public static String path; //way to main directory
    public static Table currentTable;
    public static Map<String, Integer> tableList; //map with names and numbers of elementls of tables

    private static boolean out;

    public static void main(final String[] args) {
        try {
            //path = System.getProperty("fizteh.db.dir");
            path = "C:\\Ololo";
            if (path == null) {
                throw new Exception("Enter directory");
            }
            tableList = new TreeMap<String, Integer>();
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception("directory not exist");
            }
            File[] children = dir.listFiles();
            int j = 0;
            while (j < children.length) {
                Table t = new Table(children[j].getName(), path);
                tableList.put(children[j].getName(), t.getNumberOfElements());
                j++;
                t.exit();
            }
            if (args.length > 0) {
                batch(args);
            } else {
                interactive();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void interactive() {
        out = false;
        Scanner sc = new Scanner(System.in);
        try {
            while (!out) {
                System.out.print("$ ");
                String s = sc.nextLine();
                Interpreter.doCommand(s, false);
            }
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void batch(final String[] args) {
        String arg;
        arg = args[0];
        for (int i = 1; i != args.length; i++) {
            arg = arg + ' ' + args[i];
        }
        String[] commands = arg.trim().split(";");
        try {
            for (int i = 0; i != commands.length; i++) {
                Interpreter.doCommand(commands[i], true);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}







