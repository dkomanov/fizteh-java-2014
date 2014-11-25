/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim;

import ru.fizteh.fivt.students.kalandarovshakarim.commands.database.*;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.table.*;
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

        Interpreter interpreter = new Interpreter(commands);
        int status = interpreter.exec(args);
        System.exit(status);
    }
}
