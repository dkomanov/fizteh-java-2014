package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.util.ArrayList;

public final class InputParse {
    private InputParse() {
        //
    }
    public static void parse(final String[] arg) {
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
                Commander.commandExec(current, dataBase);
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
