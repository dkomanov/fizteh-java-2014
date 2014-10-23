package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

import java.io.IOException;
import java.util.Scanner;


public class Interactive {
    private final static String PROMPT = "$ ";
    public static void conv() throws MapException, DataBaseCorrupt, TableConnectionException, IOException {
        try (Scanner in = new Scanner(System.in)) {
            DataBase dBase = new DataBase();
            
            System.out.print(PROMPT);
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
                System.out.print(PROMPT);
            }
        }
    }
}

