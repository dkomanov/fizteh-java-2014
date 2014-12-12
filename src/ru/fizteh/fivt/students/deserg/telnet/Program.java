package ru.fizteh.fivt.students.deserg.telnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by deserg on 11.12.14.
 */
public abstract class Program {

    public void work() {

        Scanner lineScan = new Scanner(System.in);
        while (lineScan.hasNext()) {

            System.out.print("$ ");
            String lineStr = lineScan.nextLine();

            String[] argumentAr = lineStr.split("\\s+");
            ArrayList<String> arguments = new ArrayList<>();

            Collections.addAll(arguments, argumentAr);
            System.out.println(execute(arguments));

        }
    }

    public abstract String execute(ArrayList<String> arguments);

}
