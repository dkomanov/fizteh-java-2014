package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class CreateCommand extends TelnetCommand {
    CreateCommand() {
        name = "create";
        numberOfArguments = 3;
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {
        if (tableProvider.isConnected()) {
            try {
                return tableProvider.sendCmd(serializeCmd(args));
            } catch (IOException e) {
                throw new CommandRuntimeException(name + ": connection error");
            }
        }
        if (args.length != numberOfArguments) {
            throw new CommandRuntimeException(name + ": wrong number of arguments");
        }
        String tableName = args[1];
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; ++i) {
            sb.append(args[i]);
            sb.append(" ");
        }
        String types = sb.toString().trim();
        if (!(types.startsWith("(") && types.endsWith(")"))) {
            throw new CommandRuntimeException(name + ": incorrect column type format");
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
                    throw new CommandRuntimeException(name + ": undefined column type '" + type + "'");
            }
            columnTypes.add(columnClass);
        }
        try (StoreableDataTable table = (StoreableDataTable) tableProvider.createTable(tableName, columnTypes)) {
            if (table == null) {
                return tableName + " exists";
            }
            return "created";
        } catch (IOException e) {
            throw new CommandRuntimeException(e.getMessage());
        }

    }
}
