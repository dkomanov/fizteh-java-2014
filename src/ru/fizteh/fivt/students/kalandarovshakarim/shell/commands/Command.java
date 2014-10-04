/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 *
 * @author Shakarim
 * @param <State>
 */
public interface Command<State> {

    String getName();

    int getArgsNum();

    void exec(State state, String args)
            throws NoSuchFileException, FileNotFoundException, IOException;
}
