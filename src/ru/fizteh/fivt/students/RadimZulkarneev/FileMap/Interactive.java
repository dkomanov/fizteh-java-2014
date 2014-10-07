package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

import java.util.Scanner;

<<<<<<< HEAD
public final class Interactive {
=======
final public class Interactive {
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix

    private Interactive() {
        //
    }
     public static void conv() {
         Scanner in = new Scanner(System.in);
         DataBase dataBase;
<<<<<<< HEAD
         try {
            dataBase = new DataBase(
                     System.getProperty("db.file"));
            
=======
        try {
            dataBase = new DataBase(
                     System.getProperty("db.file"));
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
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
<<<<<<< HEAD
                         Commander.commandExec(current, dataBase);
=======
                         Commander.CommandExec(current, dataBase);
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
                     } catch (MapExcept ex1) {
                         System.out.println(ex1.toString());
                     }
                   //  in.close();
                 }
             } catch (Exception ex) {
                 System.out.println(ex.getMessage());
<<<<<<< HEAD
                 in.close(); 
=======
                 in.close();
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
                 System.exit(1);
             }
             dataBase.writeInFile();
        } catch (MapExcept exept) {
            System.out.println(exept.toString());
            in.close();
            System.exit(1);
        }
     }
<<<<<<< HEAD
}
=======
}
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
