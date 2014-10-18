/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

/**
 *
 * @author Shakarim
 */
public class CommandParser {

    public static String[] parseArgs(String[] args) {
        String command;
        StringBuilder newArgs = new StringBuilder();

        for (String str : args) {
            newArgs.append(str).append(' ');
        }

        command = newArgs.toString();

        return command.split("\\s*;\\s*");
    }

    public static String[] getParams(String command) {
        int index = command.indexOf(' ');
        int opt = command.indexOf("-r");

        if (index == -1) {
            return new String[0];
        }
        if (opt != -1) {
            command = command.replace(" -r ", " ").concat(" -r");
        }
        command = command.substring(index + 1).trim();

        return command.split("\\s+");
    }

    public static boolean isRec(String command) {
        int opt = command.indexOf("-r");
        return opt != -1;
    }

    public static String getCmdName(String command) {
        int index = command.indexOf(' ');

        if (index == -1) {
            return command;
        }
        return command.substring(0, index);
    }
}
