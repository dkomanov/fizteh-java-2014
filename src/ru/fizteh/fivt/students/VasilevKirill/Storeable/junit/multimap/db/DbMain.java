package ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db;

/**
 * Created by Kirill on 30.09.2014.
 */

//import ru.fizteh.fivt.students.VasilevKirill.multimap.db.*;

import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Shell;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class DbMain {

    public static void main(String[] args) {
        Map<String, Command> commands = new HashMap<String, Command>();
        commands.put(new PutCommand().toString(), new PutCommand());
        commands.put(new ru.fizteh.fivt.students.VasilevKirill.db.GetCommand().toString(), new GetCommand());
        commands.put(new ru.fizteh.fivt.students.VasilevKirill.db.ListCommand().toString(), new ListCommand());
        commands.put(new ru.fizteh.fivt.students.VasilevKirill.db.RemoveCommand().toString(), new RemoveCommand());
        int retValue = 0;
        try (FileMap fileMap = new FileMap(System.getProperty("db.file"))) {
            Status newStatus = new Status(fileMap);
            if (args.length == 0) {
                new Shell(commands, newStatus).handle(System.in);
            } else {
                retValue = new Shell(commands, newStatus).handle(args);
            }
            fileMap.close();
            System.exit(retValue);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}

