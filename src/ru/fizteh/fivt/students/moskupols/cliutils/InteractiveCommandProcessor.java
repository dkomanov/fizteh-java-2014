package ru.fizteh.fivt.students.moskupols.cliutils;

import java.util.Scanner;

/**
 * Created by moskupols on 28.09.14.
 */
public class InteractiveCommandProcessor implements CommandProcessor {
    @Override
    public void process(CommandFabric commandFabric) {
        Scanner scanner = new Scanner(System.in);
        boolean needed = true;
        while (needed && scanner.hasNextLine()) {
            for (String s : scanner.nextLine().split(";")) {
                try {
                    commandFabric.fromString(s).execute();
                } catch (UnknownCommandException | CommandExecutionException e) {
                    System.err.println(e.getMessage());
                } catch (StopProcessingException e) {
                    needed = false;
                }
            }
        }
    }
}
