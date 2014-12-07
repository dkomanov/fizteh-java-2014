package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.FileMap;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;

import java.util.ArrayList;

/**
 * Created by user1 on 14.10.2014.
 */
public class UseCommand extends TableCommand implements Command{
    private boolean isSilent;
    public UseCommand(DataBaseShell shell, boolean isSilent) {
        super(shell);
        this.isSilent = isSilent;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (shell.getFileMap() != null) {
            shell.getFileMap().saveMap();
        }
        if (arguments.size() != 1) {
            throw new Exception("usage: use tableName");
        }
        if (!onExistCheck(arguments.get(0), EXIST)) {
            return;
        }
        shell.setFileMap(new FileMap(shell.getDataBaseDirectory(), arguments.get(0)));
        shell.setOpenedTableName(arguments.get(0));
        if (!isSilent) {
            System.out.println("using " + arguments.get(0));
        }
        shell.refreshCommands();
    }

    @Override
    public String getName() {
        return "use";
    }
}
