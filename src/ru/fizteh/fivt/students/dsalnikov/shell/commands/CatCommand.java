package ru.fizteh.fivt.students.dsalnikov.shell.commands;


import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.utils.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CatCommand implements Command {

    Shell link;

    public CatCommand(Shell s) {
        link = s;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws IOException {
        if (StringUtils.processFile(link.getState().getState(), args[1]).isDirectory()) {
            throw new IllegalArgumentException("can't cat directory");
        } else {
            File path = StringUtils.processFile(link.getState().getState(), args[1]);
            FileInputStream fis = new FileInputStream(path);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),
                    StandardCharsets.UTF_8))) {
                String line = br.readLine();
                while (line != null) {
                    outputStream.println(line);
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
