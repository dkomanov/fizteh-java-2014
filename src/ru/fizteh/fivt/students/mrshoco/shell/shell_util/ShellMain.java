package shell_util;

import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;

/**
 * class ShellMain.
 */
public final class ShellMain {
    /**
     * Constructor.
     */
    private ShellMain() {
    }

    /**
     * @param args
     *            programm arguments
     */
    public static void main(final String[] args) {
        if (args.length != 0) {
            int i = 0, j = 0;
            for (i = 0; i <= args.length; i++) {
                if (i == args.length || args[i].equals(";")) {
                    try {
                        Command cmd = Command.create(Arrays.copyOfRange(args,
                                j, i));
                        cmd.run();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.exit(0);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                    j = i + 1;
                } else {
                    args[i] = args[i].trim();
                }
            }
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("$ ");
            String[] input = sc.nextLine().split(";");
            while (true) {
                for (int i = 0; i < input.length; i++) {
                    input[i] = input[i].trim();
                    try {
                        Command cmd = Command.create(input[i].split(" "));
                        cmd.run();
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        sc.close();
                        System.exit(0);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                System.out.print("$ ");
                input = sc.nextLine().split(";");
            }
        }
    }
}
