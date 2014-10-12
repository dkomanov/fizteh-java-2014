package ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

import java.util.Scanner;

public class InteractiveMode implements CommandProcess {

    @Override
    public void process(CommandContainer commandFromString) throws UnknownCommand {
        Scanner sc = new Scanner(System.in);
        boolean flag = false;
        do {
            System.out.print("$ ");
            for (String s : sc.nextLine().split(";")) {
                try {
                    commandFromString.getCommandByName(s).execute();
                } catch (StopProcess e) {
                    flag = true;
                } catch (IncorrectInputException e) {
                    continue;
                }
                if (flag) {
                    break;
                }
            }
        } while (!flag);
    }
}
