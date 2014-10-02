package ru.fizteh.fivt.students.test.shell;

/**
 * Created by deserg on 21.09.14.
 */
public class Init {

    public static void main(String[] args) {
        if (args.length > 0) {


            Command command = new Command();
            command.readCommands(args);
            command.executeAll();
            if (command.isExceptionOccured()) {
                System.exit(1);
            }

        } else {
            Command command = new Command();
            do {
                command.readCommands(null);
                command.executeAll();
                if (command.isEndOfProgram()) {
                    break;
                }

            } while (true);

        }
    }

}


