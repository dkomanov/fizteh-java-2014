package ru.fizteh.fivt.students.maxim_gotovchits.file_map;

/**
 * Created by Maxim on 07.10.2014.
 */
public class BatchMode extends FileMapMain {
    void batchModeFunction(String commands)throws Exception {
        commands = commands.replaceAll("\\s+", " ");
        String [] commandsSeq = commands.split(";");
        for (int ind = 0; ind < commandsSeq.length; ++ind) {
            commandsSeq[ind] = commandsSeq[ind].replaceAll("\\W", " ");
            commandsSeq[ind] = commandsSeq[ind].replaceAll("\\s+", " ");
        }
        for (Integer bufInd = 0; bufInd < commandsSeq.length; ++bufInd) {
            if (commandsSeq[bufInd].equals("\\s+")) {
                Integer look = 0;
                for (int tmpInd = 0; tmpInd < commandsSeq.length; ++tmpInd) {
                    if (!commandsSeq[tmpInd].equals("s\\+")) {
                        commandsSeq[look] = commandsSeq[tmpInd];
                    }
                    ++look;
                }
                break;
            }
        }
        Integer parseInd = -1;
        while (parseInd < commandsSeq.length) {
            ++parseInd;
            if (parseInd >= commandsSeq.length) {
                break;
            }
            cmdBuffer = commandsSeq[parseInd].split(" ");
            if (cmdBuffer.length == 0) {
                continue;
            }
            for (Integer bufInd = 0; bufInd < cmdBuffer.length; ++bufInd) {
                cmdBuffer[bufInd] = cmdBuffer[bufInd].replaceAll("\\s+", "");
                }
            if (cmdBuffer[0].equals("") && cmdBuffer.length > 1) {
                cmdBuffer[0] = cmdBuffer[1];
            }
            if (cmdBuffer[0].equals("put")) {
                if (cmdBuffer.length == 3) {
                    new Put().putFunction();
                } else {
                    System.err.println("Incorrect syntax");
                }
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
        }
    }
}

