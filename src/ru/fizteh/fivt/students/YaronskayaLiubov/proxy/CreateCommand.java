package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class CreateCommand extends Command {
    CreateCommand() {
        name = "create";
        numberOfArguments = 3;
    }

    boolean execute(MultiFileHashMap multiFileHashMap, String[] args) throws MultiFileMapRunTimeException {
        if (args.length < numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        String tableName = args[1];
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; ++i) {
            sb.append(args[i]);
            sb.append(" ");
        }
        String types = sb.toString().trim();
        if (!(types.startsWith("(") && types.endsWith(")"))) {
            throw new MultiFileMapRunTimeException("Incorrect column type format");
        }
        types = types.substring(1, types.length() - 1);
        Scanner scanner = new Scanner(types);
        List<Class<?>> columnTypes = new ArrayList<>();
        while (scanner.hasNext()) {
            String type = scanner.next();
            Class<?> columnClass = null;
            switch (type) {
                case "int":
                    columnClass = Integer.class;
                    break;
                case "long":
                    columnClass = Long.class;
                    break;
                case "byte":
                    columnClass = Byte.class;
                    break;
                case "float":
                    columnClass = Float.class;
                    break;
                case "double":
                    columnClass = Double.class;
                    break;
                case "boolean":
                    columnClass = Boolean.class;
                    break;
                case "String":
                    columnClass = String.class;
                    break;
                default:
                    throw new MultiFileMapRunTimeException("Undefined column type '" + type + "'");
            }
            columnTypes.add(columnClass);
        }
        if (multiFileHashMap.provider.createTable(tableName, columnTypes) == null) {
            System.out.println(tableName + " exists");
        }
        return true;
    }
}
