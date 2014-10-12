package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

import java.util.*;

public class InteractiveMode extends FileMapMain {
    void interactiveModeFunction() throws Exception {
        Scanner sc = new Scanner(System.in);
        String cmd = "";
        while (true) {
            System.out.print("$ ");
            cmd = sc.nextLine();
            cmd = cmd.replaceAll("\\s+", " ");
            cmdBuffer = cmd.split(" ");
            if (cmdBuffer[0].equals("") && cmdBuffer.length > 0) {
                cmd = "";
                for (int ind = 1; ind < cmdBuffer.length; ++ind) {
                    cmd = cmd + cmdBuffer[ind] + " ";
                }
                cmdBuffer = cmd.split(" ");
            }
            if (cmdBuffer[0].equals("put")) {
                new Put().putFunction();
            }
            if (cmdBuffer[0].equals("get")) {
                new Get().getFunction();
            }
            if (cmdBuffer[0].equals("remove")) {
                new Remove().removeFunction();
            }
            if (cmdBuffer[0].equals("list")) {
                new List().listFunction();
            }
            if (cmdBuffer[0].equals("exit")) {
                new FillingDB().fillingDBFunction();
                new Exit().exitFunction();
            }
        }
    }
}

