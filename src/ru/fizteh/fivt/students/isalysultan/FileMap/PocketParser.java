package ru.fizteh.fivt.students.isalysultan.FileMap;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class PocketParser {

    PocketParser() {
        // Disable instantiation to this class.
    }

    public static void batchMode(Table object, String[] argv)
            throws IOException {
        Scanner in = new Scanner(System.in);
        int commandCount = 0;
        Commands newCommand = new Commands();
        StringBuilder allStringBuild = new StringBuilder();
        for (String argument : argv) {
            if (allStringBuild.length() != 0) {
                allStringBuild.append(' ');
            }
            allStringBuild.append(argument);
        }
        String allString = allStringBuild.toString();
        String[] commands = allString.split(";");
        int i = 0;
        CommandExecutor newParser = new CommandExecutor();
        boolean firstElement = true;
        while (i <= commands.length) {
            if (firstElement == true) {
                String[] command = commands[0].split(" ");
                newParser.execute(object, command);
                firstElement = false;
            } else {
                String[] command = commands[i].trim().split(",");
                int j = 1;
                String newString = command[0];
                while (j < command.length) {
                    newString = newString + " " + command[j];
                }
                String[] endCommand = newString.trim().split(" ");
                newParser.execute(object, endCommand);
            }
            ++i;
        }
    }
}
