package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.io.PrintStream;

public class CommandDrop extends CommandTableProviderExtended {
    public CommandDrop() {
        name = "drop";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        try {
            dataBase.removeTable(args[1]);
            System.out.println("dropped");
        } catch (IOException e) {
            System.err.println("io exception while removing directory");
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
