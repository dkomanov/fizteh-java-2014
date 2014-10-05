package ru.fizteh.fivt.students.standy66.filemap;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class containing entry point. <br>
 * Created by astepanov on 26.09.14.
 */
public class FileMapMain {
    public static void main(String[] args) {
        FileMapShell fileMapShell;
        if (args.length == 0) {
            fileMapShell = new FileMapShell(System.in, true);
        } else {
            String params = Stream.of(args).collect(Collectors.joining(" "));
            fileMapShell = new FileMapShell(new ByteArrayInputStream(params.getBytes()), false);
        }
        fileMapShell.run();
    }
}
