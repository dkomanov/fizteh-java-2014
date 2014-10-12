package ru.fizteh.fivt.students.ElinaDenisova.FileMap;

import java.util.ArrayList;

public class InputParse {
    public static void parse(String[] arg) {
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
                ParserCommands.commandExec(current, dataBase);
            }
            dataBase.writeInFile();
        } catch (DataBaseException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);

        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getLocalizedMessage());
            System.exit(1);
        }
    }
}
