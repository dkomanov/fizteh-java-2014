package ru.fizteh.fivt.students.dsalnikov.shell.Commands;


import ru.fizteh.fivt.students.dsalnikov.Utils.StringUtils;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
            try (BufferedReader br = new BufferedReader(new FileReader(StringUtils.ProcessFile(link.getState().getState(),
                    args[1])))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                System.out.println(everything);
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
