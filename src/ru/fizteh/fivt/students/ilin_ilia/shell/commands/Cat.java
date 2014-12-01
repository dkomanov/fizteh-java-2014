package ru.fiztech.fivt.students.theronsg.shell.commands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class Cat {
    public static void run(final String fileName) {
        String s = "";
        try {
            FileReader fileread =  new FileReader(fileName);
            BufferedReader bufreader = new BufferedReader(fileread);
            while ((s = bufreader.readLine()) != null) {
                System.out.println(s);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File \"" + fileName + "\" not found");
            return;
        } catch (IOException e) {
            System.out.println("Can't read file");
      }
    }

}
