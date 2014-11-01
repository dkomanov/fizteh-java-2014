package ru.fizteh.fivt.students.standy66_new.exceptions;

import java.io.IOException;

/**
 * Created by astepanov on 20.10.14.
 */
public class FileMapCorruptedFile extends IOException {
    public FileMapCorruptedFile(String message) {
        super(message);
    }
}
