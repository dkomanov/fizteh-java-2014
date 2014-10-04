package ru.fizteh.fivt.students.standy66.shell;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by astepanov on 21.09.14.
 */
public class CatAction extends Action {
    public CatAction(String[] args) {
        super(args);
    }

    @Override
    public boolean run() {
        try {
            Scanner sc = new Scanner(new FileInputStream(FileUtils.fromPath(arguments[1])));
            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
        } catch (IOException e) {
            System.err.printf("cat: %s: No such file or directory", arguments[1]);
            return false;
        }
        return true;
    }
}
