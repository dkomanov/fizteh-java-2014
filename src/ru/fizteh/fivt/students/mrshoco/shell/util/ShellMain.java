package util;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 *
 * @author
 */
public final class ShellMain {
    /**
     * {@inheritDoc}
     *
     * @see Object#ShellMain()
     */
    private ShellMain() {
    }

    /**
     *
     *
     * @param args
     */
    public static void main(final String[] args) {
        if (args.length != 0) {
            int i = 0;
            int j = 0;
            for (i = 0; i <= args.length; i++) {
                if (i == args.length || args[i].equals(";")) {
                    try {
                        Command cmd = Command.create(Arrays.copyOfRange(args,
                                j, i));
                        cmd.run();
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
            try {
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
            } catch (NoSuchElementException e) {
                System.out.print("Exit");
            }
        }
    }
}
