package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.commands;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils.TableException;

import java.io.File;

public class ShowCommand implements Command {
    public void execute(String[] args) throws TableException, Exception {
        if (!args[1].equals("tables")) {
            throw new Exception("The name of this command is \"show tables\"");
        }
        for (File file: DataBase.root.listFiles()) {
            String tableName = file.getName().toString();
            int valuesNumber;
            if (DataBase.tables.containsKey(tableName)) {
                valuesNumber = DataBase.tables.get(tableName).intValue();
            } else {
                throw new TableException("There is table which we don't know");
            }
            System.out.println(tableName + " " + valuesNumber);
        }
    }

    public String name() {
        return "show";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
