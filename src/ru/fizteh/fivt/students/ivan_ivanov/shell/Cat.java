package ru.fizteh.fivt.students.ivan_ivanov.shell;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class Cat implements Command {

    public final String getName() {

        return "cat";
    }

    public static void catenate(final String fileName) {
        String buffer = "";
        try (FileReader filescan =  new FileReader(fileName);
                BufferedReader bufreader = new BufferedReader(filescan)) {
            while ((buffer = bufreader.readLine()) != null) {
                System.out.println(buffer);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File \"" + fileName + "\" not found");
            return;
        } catch (IOException e) {
            System.out.println("Can't read file");
      }
    }

    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        Path thePath = shell.getState().getPath().resolve(args[0]);
        try {
            if (1 == args.length) {
                if (thePath.toFile().exists()) {
                    catenate(thePath.toString());
                }
            } else {
                throw new IOException("not allowed number of arguments");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            while (!thePath.toFile().isDirectory()) {
                thePath = thePath.getParent();
            }
            shell.setState(thePath);
        }
    }
}


