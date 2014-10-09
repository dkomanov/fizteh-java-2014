import java.io.File;
import java.util.NoSuchElementException;
import java.util.Scanner;

import util.*;

public final class FileMap {

    public static void main(final String[] args) {
        File fl = new File(System.getProperty("db.file"));
        if (args.length != 0) {
        } else {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print("$ ");
                String input = sc.nextLine();
                sc.close();
            } catch (NoSuchElementException e) {
                System.out.print("Exit");
            }
        }
    }
}
