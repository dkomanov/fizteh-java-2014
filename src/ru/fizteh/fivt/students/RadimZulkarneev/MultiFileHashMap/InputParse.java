package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.IOException;
import java.util.ArrayList;


public final class InputParse {
    private InputParse() {
        //
    }
    public static void parse(final String[] arg) throws DataBaseCorrupt, 
        TableConnectionException, MapException, IOException {

            DataBase dataBase = new DataBase();

            ArrayList<String> singleCommand = new ArrayList<String>();
            for (int i = 0; i < arg.length; ++i) {
                singleCommand.clear();
                while (i < arg.length) {
                    if (!(arg[i].indexOf(";") >= 0)) {
                        singleCommand.add(arg[i]);
                        i++;
                    } else {
                        singleCommand.add(arg[i].substring(
                                0, arg[i].indexOf(";")));
                        break;
                    }
                }
                try {
                    Commander.command(singleCommand, dataBase);
                } catch (IllegalStateException e) {
                    System.exit(0);
                } catch (IndexOutOfBoundsException | IOException
                        | DataBaseCorrupt | TableConnectionException | MapException e) {
                    // TODO Auto-generated catch block
                    System.out.println(e.toString());
                    System.exit(1);
                }
            }
            singleCommand.clear();
            try {
                singleCommand.add("exit");
                Commander.command(singleCommand, dataBase);
            } catch (IndexOutOfBoundsException | IOException
                    | DataBaseCorrupt | TableConnectionException | MapException e) {
                System.exit(1);
            }
    }
}
