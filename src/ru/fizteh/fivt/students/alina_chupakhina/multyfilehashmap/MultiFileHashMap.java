package ru.fizteh.fivt.students.alina_chupakhina.multyfilehashmap;

import java.io.*;
import java.util.*;

public class MultiFileHashMap {

    public static String path; // Way to main directory
    public static Map<String, Integer> tableList; // Map with names and numbers of elementls of tables


    public static void main(final String[] args) {
        try {
            path = System.getProperty("fizteh.db.dir");
            if (path == null) {
                throw new Exception("Enter directory");
            }
            tableList = new TreeMap<>();
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception("directory not exist");
            }
            File[] children = dir.listFiles();
            for (File child : children) {
                Table t = new Table(child.getName(), path);
                tableList.put(child.getName(), t.getNumberOfElements());
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
        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("$ ");
                String [] s = sc.nextLine().trim().split(";");
                for (String command : s) {
                    Interpreter.doCommand(command, false);
                }
            }
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








