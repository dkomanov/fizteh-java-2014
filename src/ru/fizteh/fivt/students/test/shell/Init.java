package ru.fizteh.fivt.students.deserg.shell;

/**
 * Created by deserg on 21.09.14.
 */
public class Init {

    public static void main(String[] args) {
        if (args.length > 0) {


            Command command = new Command();
            command.readCommands(args);

            try {
                command.executeAll();
            } catch (MyException ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }


        } else {
            Command command = new Command();
            do {
                command.readCommands(null);
                try {
                    command.executeAll();
                } catch (MyException ex) {
                    System.out.println(ex.getMessage());
                }

                if (command.isEndOfProgram()) {
                    break;
                }

            } while (true);

        }
    }

}


