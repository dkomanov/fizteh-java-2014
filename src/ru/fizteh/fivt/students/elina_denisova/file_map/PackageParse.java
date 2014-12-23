package ru.fizteh.fivt.students.elina_denisova.file_map;

import java.util.ArrayList;

public class PackageParse {
    public static void parse(DataBase dataBase, String[] arg) {
        try {
            ArrayList<String> current = new ArrayList<String>();
            for (int i = 0; i < arg.length; ++i) {
                current.clear();
                while (i < arg.length) {
                    if (!(arg[i].contains(";"))) {
                        current.add(arg[i]);
                        i++;
                    } else {
                        current.add(arg[i].substring(0, arg[i].indexOf(";")));
                        break;
                    }
                }
                ParserCommands.commandsExecution(current, dataBase);
            }
            dataBase.writeInFile();
        } catch (IllegalMonitorStateException e) {
            dataBase.writeInFile();
            System.out.println("Goodbye");
            System.exit(0);
        } catch (IllegalArgumentException e) {
            dataBase.writeInFile();
            HandlerException.handler("PackageParse: Wrong arguments", e);
        } catch (Exception e) {
            dataBase.writeInFile();
            HandlerException.handler("PackageParse: Unknown error", e);
        }
    }
}
