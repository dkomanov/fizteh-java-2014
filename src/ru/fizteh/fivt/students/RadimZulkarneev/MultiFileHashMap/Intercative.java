package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.util.Scanner;


public class Intercative {
    public static void conv() throws MapExcept, DataBaseCorrupt, TableConnectionError {
        try (Scanner in = new Scanner(System.in)) {
            DataBase dBase = new DataBase();
            System.out.print("$ ");
            while (in.hasNextLine()) {
                String s;
                s = in.nextLine();
                s = s.trim();
                String[] current = s.split("\\s+");
                for (int i = 0; i < current.length; ++i) {
                    current[i].trim();
                }
                try {
                    Commander.command(current, dBase);
                } catch (IllegalStateException ex) {
                    return;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.out.println(current[0] + ": missing operand");
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
                System.out.print("$ ");
            }
        }
    }
}

