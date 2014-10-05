package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.util.Scanner;

public final class Interactive {

    private Interactive() {
        //
    }
     public static void conv() {
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
                         Commander.commandExec(current, dataBase);
                     } catch (MapExcept ex1) {
                         System.out.println(ex1.toString());
                     }
                   //  in.close();
                 }
             } catch (Exception ex) {
                 System.out.println(ex.getMessage());
                 in.close(); 
                 System.exit(1);
             }
             dataBase.writeInFile();
        } catch (MapExcept exept) {
            System.out.println(exept.toString());
            in.close();
            System.exit(1);
        }
     }
}
