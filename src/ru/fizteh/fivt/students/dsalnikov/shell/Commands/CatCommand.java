package ru.fizteh.fivt.students.dsalnikov.shell.Commands;


import ru.fizteh.fivt.students.dsalnikov.utils.StringUtils;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CatCommand implements Command {

    Shell link;

    public CatCommand(Shell s) {
        link = s;
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Wrong amount of arguments");
        } else if (StringUtils.ProcessFile(link.getState().getState(), args[1]).isDirectory()) {
            throw new IllegalArgumentException("can't cat directory");
        } else {
            File path = StringUtils.ProcessFile(link.getState().getState(), args[1]);
            FileInputStream fis = new FileInputStream(path);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
                String line = br.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = br.readLine();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "cat";

    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
