package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class ListTableCommand extends TableCommand {
    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            List<String> list = ((StructuredTable) base.getUsing()).list();
            StringBuilder result = new StringBuilder();
            for (String key: list) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append(key);
            }
            System.out.println(result.toString());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
