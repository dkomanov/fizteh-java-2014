package ru.fizteh.fivt.students.irina_karatsapova.shell;

import ru.fizteh.fivt.students.irina_karatsapova.shell.commands.*;

import java.util.Scanner;

public class Shell {
    private Commander commander = new Commander();
    
    public void addCommand(Command command) {
        commander.addCommand(command);
    }
    
    public void interactiveMode() {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = null;
            if (in.hasNextLine()) {
                input = in.nextLine();
            }
            else {
                break;
            }
            try {
                batchMode(input);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    public void batchMode(String arg) throws Exception {
        String[] commands = arg.split(";");
        for (String command : commands) {
            commander.startCommand(command);
        }
    }
}

