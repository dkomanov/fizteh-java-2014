package ru.fizteh.fivt.students.standy66_new.exceptions;

import java.io.IOException;

/**
 * Created by astepanov on 20.10.14.
 */
public class FileCorruptedException extends IOException {
    public FileCorruptedException(String message) {
        super(message);
    }
}
