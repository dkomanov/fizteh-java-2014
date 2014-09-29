package com.Shell.commads;

/**
 * Created by deserg on 21.09.14.
 */
public class Init {

    public static void main(String[] args) {
        if (args.length > 0) {


            Command command = new Command();
            command.readCommands(args);
            command.executeAll();

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
