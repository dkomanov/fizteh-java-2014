package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.util.ArrayList;

<<<<<<< HEAD
public final class InputParse {
    private InputParse() {
        //
    }
    public static void parse(final String[] arg) {
=======
final public class InputParse {
    private InputParse() {
        //
    }
    public static void Parse(final String[] arg) {
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
        try {
            DataBase dataBase = new DataBase(
                    System.getProperty("db.file"));

            ArrayList<String> current = new ArrayList<String>();
            for (int i = 0; i < arg.length; ++i) {
                current.clear();
                while (i < arg.length) {
                    if (!(arg[i].indexOf(";") >= 0)) {
                        current.add(arg[i]);
                        i++;
                    } else {
                        current.add(arg[i].substring(
                                0, arg[i].indexOf(";")));
                        break;
                    }
                }
<<<<<<< HEAD
                Commander.commandExec(current, dataBase);
=======
                Commander.CommandExec(current, dataBase);
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
            }
            dataBase.writeInFile();
        } catch (MapExcept ex) {
            if (ex.getMessage().equals(
                    "DataBase: I don't know-exception")) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }
        }
    }
}
