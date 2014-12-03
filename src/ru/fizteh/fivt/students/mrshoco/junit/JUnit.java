import java.util.Scanner;

import strings.*;

import util.*;

public final class JUnit {

    public static void main(final String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        TableProviderFactory tableProviderFactory = new MyTableProviderFactory();
        TableProvider tableProvider = null;
        try {
            tableProvider = tableProviderFactory.create(path);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        TableLauncher tableLauncher = new TableLauncher(tableProvider);

        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(tableLauncher.getCurrentName() + "$ ");
                String[] input = sc.nextLine().split(";");
                for (int i = 0; i < input.length; i++) {
                    input[i] = input[i].trim();
                    tableLauncher.run(input[i].split(" "));
                }
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
