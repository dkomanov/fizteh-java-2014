package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.IOException;
import java.util.ArrayList;


public class InputParse {
    private InputParse() {
        //
    }
    public static void parse(final String[] arg) throws DataBaseCorrupt, TableConnectionError, MapExcept {

            DataBase dataBase = new DataBase();

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
                try {
                    Commander.commandExec(current, dataBase);
                } catch (IndexOutOfBoundsException | IOException
                        | DataBaseCorrupt | TableConnectionError | MapExcept e) {
                    // TODO Auto-generated catch block
                    System.out.println(e.toString());
                    System.exit(1);
                }
            }
    }
}
