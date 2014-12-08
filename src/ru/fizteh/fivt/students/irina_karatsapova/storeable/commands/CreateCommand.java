package ru.fizteh.fivt.students.irina_karatsapova.storeable.commands;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.TypeTransformer;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws Exception {
        if (!Utils.checkNoChanges(tableProvider)) {
            return;
        }
        String tableName = args[1];
        List<Class<?>> types = parseTypes(args);
        Table createdTable = tableProvider.createTable(tableName, types);
        if (createdTable == null) {
            System.out.println(tableName + " exists");
        } else {
            System.out.println("created");
        }
    }

    private List<Class<?>> parseTypes(String[] args) throws Exception {
        List<Class<?>> types = new ArrayList<>();
        List<String> typeNames = new ArrayList<>();
        for (int argsIndex = 2; argsIndex < args.length; ++argsIndex) {
            typeNames.add(args[argsIndex]);
        }
        typeNames = deleteCommas(typeNames);
        for (String typeName: typeNames) {
            types.add(TypeTransformer.getTypeByName(typeName));
        }
        return types;
    }

    private List<String> deleteCommas(List<String> typeNames) throws Exception {
        int firstIndex = 0;
        if (typeNames.get(firstIndex).startsWith("(")) {
            typeNames.add(firstIndex, typeNames.get(firstIndex).substring(1));
            typeNames.remove(firstIndex + 1);
        } else {
            throw new Exception("Wrong format of command");
        }

        int lastIndex = typeNames.size() - 1;
        if (typeNames.get(lastIndex).endsWith(")")) {
            int length = typeNames.get(lastIndex).length();
            typeNames.add(lastIndex, typeNames.get(lastIndex).substring(0, length - 1));
            typeNames.remove(lastIndex + 1);
        } else {
            throw new Exception("Wrong format of command");
        }

        return typeNames;
    }

    public String name() {
        return "create";
    }

    public int minArgs() {
        return 3;
    }

    public int maxArgs() {
        return Integer.MAX_VALUE;
    }
}
