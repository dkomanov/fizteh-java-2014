package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;

import java.util.Map;
import java.util.Set;

/**
 * Created by mikhail on 19.10.14.
 */
public class CommandShowTables extends CommandMultiFileHashMap {
    public CommandShowTables() {
        name = "show tables";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }

        System.out.println("table_name row_count");
        myMap.showTables();
        return true;
    }
}
