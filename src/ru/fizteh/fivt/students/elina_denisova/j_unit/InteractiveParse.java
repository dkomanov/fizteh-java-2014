package ru.fizteh.fivt.students.elina_denisova.j_unit;

import java.util.Scanner;

public class InteractiveParse {
    public static void parse(MyTableProvider directory) {
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("$ ");
                String s;
                s = in.nextLine();
                s = s.trim();
                String[] current = s.split("\\s+");
                for (int i = 0; i < current.length; ++i) {
                    current[i].trim();
                }
                ParserCommands.commandsExecution(current, directory);
            }
        } catch (IllegalMonitorStateException e) {
            in.close();
            System.out.println("Goodbye");
            System.exit(0);
        } catch (Exception e) {
            in.close();
            HandlerException.handler("InteractiveParse: Unknown error", e);
        }
        in.close();
    }
}
