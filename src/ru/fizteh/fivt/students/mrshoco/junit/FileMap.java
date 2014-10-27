import util.*;

import java.io.File;
import java.util.Scanner;

public final class FileMap {

    public static void main(final String[] args) {
        File file = null;
        try {
            file = new File(System.getProperty("fizteh.db.dir"));
        } catch (Exception e) {
            System.err.println("Directory doesnt exist");
            System.exit(1);
        }
        TableLauncher tableLauncher = null;
        try {
            tableLauncher = new TableLauncher(file);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(tableLauncher.getCurrentDb() + "$ ");
                String input = sc.nextLine().trim();
                tableLauncher.run(input.split(" "));
            } catch (ExitException e) {
                sc.close();
                System.out.println(e.getMessage());
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
