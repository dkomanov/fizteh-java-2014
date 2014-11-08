/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import java.io.IOException;

/**
 *
 * @author Shakarim
 */
public interface Command {

    String getName();

    int getArgsNum();

    void exec(String[] args) throws IOException;
}
