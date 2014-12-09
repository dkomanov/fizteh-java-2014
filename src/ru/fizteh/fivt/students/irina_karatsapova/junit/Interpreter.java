package ru.fizteh.fivt.students.irina_karatsapova.junit;

import ru.fizteh.fivt.students.irina_karatsapova.junit.commands.Command;
import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.DataBaseException;

import java.util.Scanner;

public class Interpreter {
    private Commander commander = new Commander();
    
    public void addCommand(Command command) {
        commander.addCommand(command);
    }
    
    public void interactiveMode(TableProvider tableProvider) {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = null;
            if (in.hasNextLine()) {
                input = in.nextLine();
            } else {
                break;
            }
            try {
                batchMode(tableProvider, input);
            } catch (DataBaseException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    public void batchMode(TableProvider tableProvider, String arg) throws Exception {
        String[] commands = arg.split(";");
        for (String command : commands) {
            commander.startCommand(tableProvider, command);
        }
    }
}

