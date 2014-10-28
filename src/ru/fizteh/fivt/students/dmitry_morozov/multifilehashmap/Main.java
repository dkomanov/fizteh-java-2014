package ru.fizteh.fivt.students.dmitry_morozov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public class Main {

    /**
     * @param args
     * @throws IOException
     */

    private static String dirPath;
    private static String curTname = "";
    private static MultiFileHashMap currentDB = null;
    private static FileMap maps = null;

    public static String use(String tablename) throws Exception {
        String toParse = maps.get(tablename);
        if (toParse.charAt(0) == 'f') { // Database found.
            String[] parsed = toParse.split("\n");
            if (parsed.length < 2) {
                System.err.print("Empty table name is not permitted");
                return "";
            }
            if (currentDB != null) {
                currentDB.exit();
            }
            currentDB = new MultiFileHashMap(parsed[1]);
            curTname = tablename;
            return "using " + tablename;
        } else {
            return tablename + " not exists\n";
        }

    }

    public static String create(String tablename) throws Exception {
        if (maps.get(tablename).charAt(0) == 'f') {
            return tablename + " exists";
        } else {
            maps.put(tablename, dirPath + "/" + tablename);
            File dir = new File(dirPath + "/" + tablename);
            dir.mkdirs();
            return "created";
        }
    }

    public static void showTables() throws IOException { // Actually doesn't
                                                         // throw anything.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        maps.list(pw);
        String tablenames = sw.toString();
        String[] tnames = tablenames.split(", ");
        for (String str : tnames) {
            System.out.println(str);
        }
        sw.close();
    }

    public static boolean removeDirectory(String path) { // returns false if
        // something was wrong
        File dir = new File(path);
        if (!dir.exists()) {
            System.err.println("table has been already deleted from disk");
            return false;
        }
        if (!dir.isDirectory()) {
            System.err.println("table directory has been damaged");
            return false;
        }
        File[] flist = dir.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                if (0 == flist[i].listFiles().length) {
                    if (!flist[i].delete()) {
                        return false;
                    }
                } else {
                    if (!removeDirectory(flist[i].getAbsolutePath())) {
                        return false;
                    }
                }
            } else {
                if (!flist[i].delete()) {
                    return false;
                }
            }

        }
        return dir.delete();
    }

    public static String drop(String tablename) {
        String got = maps.get(tablename);
        String res = "";
        if (got.charAt(0) == 'f') { // Database found.
            String tname = got.split("\n")[1];
            if (!removeDirectory(tname)) {
                System.err.println("deleting table from disk failed");
            } else {
                maps.remove(tablename);
                res = "dropped";
            }
            if (tablename.equals(curTname)) {
                currentDB = null;
                curTname = "";
            }
        } else {
            res = "tablename not exists";
        }
        return res;
    }

    public static boolean functionHandler(String[] comAndParams, int bIndex,
            int eIndex) throws Exception { // Returns false if
                                           // exit command got.
        boolean res = true;

        String com = comAndParams[bIndex];
        if (com.equals("exit")) {
            res = false;
            if (currentDB == null) {
                return false;
            }
            try {
                currentDB.exit();
            } catch (IOException e) {
                throw e;
            }
        } else if (com.equals("put")) {
            if (currentDB == null) {
                System.out.println("no table");
                return true;
            }
            if (bIndex + 3 > eIndex) {
                System.out.println("Not enough parametres for put");
            } else {
                System.out.println(currentDB.put(comAndParams[bIndex + 1],
                        comAndParams[bIndex + 2]));
            }
        } else if (com.equals("remove")) {
            if (currentDB == null) {
                System.out.println("no table");
                return true;
            }
            if (bIndex + 2 > eIndex) {
                System.out.println("Not enough parametres for remove");
            } else {
                System.out.println(currentDB.remove(comAndParams[bIndex + 1]));
            }
        } else if (com.equals("get")) {
            if (currentDB == null) {
                System.out.println("no table");
                return true;
            }
            if (bIndex + 2 > eIndex) {
                System.out.println("Not enough parametres for get");
            } else {
                System.out.println(currentDB.get(comAndParams[bIndex + 1]));
            }
        } else if (com.equals("list")) {
            if (currentDB == null) {
                System.out.println("no table");
                return true;
            }
            PrintWriter pw = new PrintWriter(System.out);
            currentDB.list(pw);
        } else if (com.equals("use")) {
            if (eIndex - bIndex < 1) {
                System.out.println("Enter database name");
            } else {
                System.out.println(use(comAndParams[bIndex + 1]));
            }
        } else if (com.equals("drop")) {
            if (eIndex - bIndex < 1) {
                System.out.println("Enter database name");
            } else {
                System.out.println(drop(comAndParams[bIndex + 1]));
            }
        } else if (com.equals("create")) {
            if (eIndex - bIndex < 1) {
                System.out.println("Enter database name");
            } else {
                System.out.println(create(comAndParams[bIndex + 1]));
            }
        } else if (com.equals("show")) {
            if (eIndex - bIndex < 2) {
                System.out.println("Show what?  My love? I don't love you.");
            } else {
                if (!comAndParams[bIndex + 1].equals("tables")) {
                    System.out.println("Don't know such word, is it a damn?");
                } else {
                    showTables();
                }
            }
        } else {
            System.out.println("Command not found");
        }

        return res;
    }

    public static boolean commandSplitting(String command) throws Exception { // Returns
                                                                              // false
                                                                              // if
                                                                              // exit
                                                                              // command
                                                                              // got.
        String[] firstSplitted = command.split(" ");
        String[] toGive = new String[firstSplitted.length];
        int j = 0;
        for (int i = 0; i < firstSplitted.length; i++) {
            if (firstSplitted[i].length() > 0) {
                toGive[j] = firstSplitted[i];
                j++;
            }
        }
        if (0 == j) {
            return true;
        }
        return functionHandler(toGive, 0, j);
    }

    public static void batchMode(String[] args, String path) throws IOException {
        if (null == path) {
            System.err.println("System property db.file is undefined");
            System.exit(1);
        }
        String currentLine = "";
        for (int i = 0; i < args.length; i++) {
            currentLine += args[i] + " ";
        }
        String[] commands = currentLine.split(";");
        try {
            FileMap fm = new FileMap(path);
            boolean exitCode = true;
            for (int i = 0; i < commands.length; i++) {
                if (commands[i].length() > 0) {
                    exitCode = commandSplitting(commands[i]);
                    if (!exitCode) {
                        break;
                    }
                }
            }
            if (exitCode) {
                fm.exit();
            }
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        dirPath = System.getProperty("fizteh.db.dir");
        // final String tablesnfo
        if (dirPath == null) {
            System.out.println("System property db.file is undefined");
        }
        if (dirPath.endsWith("/") && !dirPath.equals("/")) {
            String tmp = "";
            for (int i = 0; i < dirPath.length() - 1; i++) {
                tmp += dirPath.charAt(i);
            }
            dirPath = tmp;
        }
        try {
            maps = new FileMap(dirPath + "/tables_info.dat");
        } catch (Exception e) {
            System.err.println("I caught it!");
            e.printStackTrace();
        }
        if (0 != args.length) {
            batchMode(args, dirPath);
        }
        Scanner in = new Scanner(System.in);

        try {
            // FileMap fm = new FileMap(path);
            boolean contFlag = true;
            while (contFlag) {
                System.out.print(curTname + " $ ");
                String currentLine = in.nextLine();
                String[] commands = currentLine.split(";");
                for (int i = 0; i < commands.length; i++) {
                    if (commands[i].length() > 0) {
                        contFlag = commandSplitting(commands[i]);
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            in.close();
            maps.exit();
        }
    }
}
