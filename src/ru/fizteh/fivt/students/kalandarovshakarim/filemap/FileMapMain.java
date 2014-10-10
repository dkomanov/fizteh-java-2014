/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap;

import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands.*;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.OneTableBase;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.SingleFileTable;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.Shell;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.Command;

/**
 *
 * @author shakarim
 */
public class FileMapMain {

    public static void main(String[] args) {
        String fileName = System.getProperty("db.file");
        OneTableBase base = new OneTableBase();
        SingleFileTable fileMap = null;

        try {
            fileMap = new SingleFileTable(fileName);
        } catch (NullPointerException ex) {
            System.out.println("db.file is not specified");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        base.setActiveTable(fileMap);

        Command[] commands = new Command[]{
            new PutCommand(base),
            new GetCommand(base),
            new ListCommand(base),
            new ExitCommand(base),
            new RemoveCommand(base)
        };

        Shell shell = new Shell(args, commands);
        shell.exec();
    }
}
