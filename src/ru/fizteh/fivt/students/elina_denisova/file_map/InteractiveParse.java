package ru.fizteh.fivt.students.elina_denisova.file_map;

import java.util.Scanner;

public class InteractiveParse {
    public static void parse(DataBase dataBase) {
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
                ParserCommands.commandsExecution(current, dataBase);
            }
        } catch (IllegalMonitorStateException e) {
            in.close();
            dataBase.writeInFile();
            System.out.println("Goodbye");
            System.exit(0);
        } catch (Exception e) {
            in.close();
            dataBase.writeInFile();
            HandlerException.handler("InteractiveParse: Unknown error", e);
        }
        in.close();
        dataBase.writeInFile();
    }
}
