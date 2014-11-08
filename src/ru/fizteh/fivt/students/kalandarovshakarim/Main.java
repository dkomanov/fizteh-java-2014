/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim;

import ru.fizteh.fivt.students.kalandarovshakarim.commands.database.UseCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.database.ShowTablesCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.database.DropCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.database.CreateCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.CommitCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.RollbackCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.ExitCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.GetCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.PutCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.ListCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.RemoveCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.SizeCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBase;
import ru.fizteh.fivt.students.kalandarovshakarim.interpeter.Interpreter;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.Command;

/**
 *
 * @author shakarim
 */
public class Main {

    public static void main(String[] args) {
        DataBase dataBase = null;
        try {
            dataBase = new DataBase(System.getProperty("fizteh.db.dir"));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Command[] commands = new Command[]{
            new PutCommand(dataBase),
            new GetCommand(dataBase),
            new UseCommand(dataBase),
            new ListCommand(dataBase),
            new SizeCommand(dataBase),
            new ExitCommand(dataBase),
            new DropCommand(dataBase),
            new CreateCommand(dataBase),
            new RemoveCommand(dataBase),
            new CommitCommand(dataBase),
            new RollbackCommand(dataBase),
            new ShowTablesCommand(dataBase)
        };

        Interpreter shell = new Interpreter(commands, args);
        shell.exec();
    }
}
