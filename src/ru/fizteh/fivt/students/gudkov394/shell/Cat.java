package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*проверено, работает*/
public class Cat {
    public Cat(final String[] currentArgs, final CurrentDirectory cd) {
        if (currentArgs.length > 2) {
            System.err.println("extra arguments for Cat");
            System.exit(1);
        }
        String read = "";
        File f = new File(cd.getCurrentDirectory(), currentArgs[1]);
        if (!f.exists()) {
            System.err.println("File is not exists" + currentArgs[1]);
            System.exit(2);
        }
        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                read = sc.nextLine();
                System.out.println(read);
            }
        } catch (FileNotFoundException e) {
            System.err.println("problem in cat with File ");
        }

    }
}
