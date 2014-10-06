package ru.fizteh.fivt.students.NikolaiKrivchanskii.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1.*;

public class CatCommand implements Commands<ShellState> {
    
    public String getCommandName() {
        return "cat";
    }

    public int getArgumentQuantity() {
        return 1;
    }
    
    public static File processFile(String currdir, String s) throws SomethingIsWrongException {
        File fi = new File(s);
        if (!fi.isAbsolute()) {
            fi = new File(currdir, s);
        }
        if (!fi.exists()) {
            throw new SomethingIsWrongException("file doesn't exist");
        }
        return fi;
    }
    
    
    public void implement(String[] args, ShellState state) throws SomethingIsWrongException {
        if (args.length != 1) {
            throw new SomethingIsWrongException("Wrong amount of arguments");
        } else if (processFile(state.getCurDir(), args[0]).isDirectory()) {
            throw new IllegalArgumentException("can't cat directory");
        } else {
            File path = processFile(state.getCurDir(), args[0]);
            try {
            FileInputStream fis = new FileInputStream(path);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),
                                                                              StandardCharsets.UTF_8))) {
                String line = br.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = br.readLine();
                }
            }
            } catch (FileNotFoundException e) {
                throw new SomethingIsWrongException(e.getMessage());
            } catch (IOException e) {
                throw new SomethingIsWrongException(e.getMessage());
            }
        }
    }
}
