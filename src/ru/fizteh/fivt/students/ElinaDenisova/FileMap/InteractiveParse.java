package ru.fizteh.fivt.students.ElinaDenisova.FileMap;

import java.util.Scanner;

public class InteractiveParse {
     public static void parse() {
         Scanner in = new Scanner(System.in);
         DataBase dataBase;
        try {
            dataBase = new DataBase(
                     System.getProperty("db.file"));
            
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
                     try {
                         ParserCommands.commandExec(current, dataBase);
                     } catch (DataBaseException ex1) {
                         System.err.println(ex1.toString());
                     } catch (IllegalArgumentException ex) {
                         System.err.println(ex.getLocalizedMessage());

                     }
                 }
             } catch (Exception ex) {
                 System.err.println(ex.getMessage());
                 in.close(); 
                 System.exit(1);
             }
             dataBase.writeInFile();
        } catch (DataBaseException exept) {
            System.err.println(exept.toString());
            in.close();
            System.exit(1);
        }
     }
}
