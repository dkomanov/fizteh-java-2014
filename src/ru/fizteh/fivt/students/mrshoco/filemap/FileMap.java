import util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public final class FileMap {

    public static void main(final String[] args) {
        File fl = new File("db.dat"/*System.getProperty("db.file")*/);
        HashMap<String, String> hm;
        try {
            hm = Data.load(fl);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            System.out.println("Error while reading file");
            return;
        }
        Launcher lnch = new Launcher(hm);
        if (args.length != 0) {
            try {
                lnch.run(args);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            Scanner sc = new Scanner(System.in);
            while (true) {
                try {
                        System.out.print("$ ");
                        String input = sc.nextLine().trim();
                        lnch.run(input.split(" "));
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    sc.close();
                    System.out.println(e.getMessage());
                    break;
                }
            }
        }

        try {
            Data.save(fl, hm);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            System.out.println("Error while reading file");
            return;
        }
    }
}
