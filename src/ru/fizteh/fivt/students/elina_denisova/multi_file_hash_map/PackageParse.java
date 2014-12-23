package ru.fizteh.fivt.students.elina_denisova.multi_file_hash_map;

import java.util.ArrayList;

public class PackageParse {
    public static void parse(MyTableProvider directory, String[] arg) {
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
                ParserCommands.commandsExecution(current, directory);
            }
            directory.getUsing().commit();
        } catch (IllegalMonitorStateException e) {
            System.out.println("Goodbye");
            System.exit(0);
        } catch (IllegalArgumentException e) {
            HandlerException.handler("PackageParse: Wrong arguments", e);
        } catch (Exception e) {
            HandlerException.handler("PackageParse: ", e);
        }
    }
}
