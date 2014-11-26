package fizteh.fivt.students.MaximGotovchits.Shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BatchMode {
    boolean tablesIsChosen = false; // Determines if table is chosen.
    Path dataBaseName = Paths.get(System.getProperty("fizteh.db.dir"));
    String[] cmdBuffer = new String[1024];
    Boolean isCommand = false;
    String tableName;
    static Map<String, String> storage = new HashMap<String, String>();
    void batchModeFunction(String commands) throws Exception {
        commands = commands.replaceAll("\\s+" + ";" + "\\s+", ";");
        commands = commands.replaceAll("\\s+", " ");
        commands = commands.replaceAll(";", " ");
        commands = commands.replaceAll("\\s+", " ");
        String[] cmdBuffer = commands.split(" ");
        System.out.println(commands);
        int parseInd = 0;
        while (parseInd < cmdBuffer.length) {
            if (cmdBuffer[0].equals("mkdir") && cmdBuffer.length == 2) {
                isCommand = true;
                new CreateDirectory().createDirectoryFunction(cmdBuffer[1]);
            }

            if (cmdBuffer[0].equals("cd") && cmdBuffer.length == 2) {
                isCommand = true;
                new ChangeDirectory().changeDirectoryFunction(cmdBuffer[1]);
            }
            if (cmdBuffer[0].equals("rm")) {
                if (cmdBuffer.length == 2) {
                    isCommand = true;
                    new Remove().removeFunction(cmdBuffer[1]);
                }
                if (cmdBuffer.length == 3) {
                    if (cmdBuffer[1].equals("-r")) {
                        isCommand = true;
                        new Remove().removeRecursivelyFunction(cmdBuffer[2]);
                    }
                }
            }
            if (cmdBuffer[0].equals("cp")) {
                if (cmdBuffer[0].equals("cp")) {
                    if (cmdBuffer.length == 3) {
                        isCommand = true;
                        new Copy().copyFunction(cmdBuffer[1], cmdBuffer[2]);
                    }
                    if (cmdBuffer.length == 4) {
                        if (cmdBuffer[1].equals("-r")) {
                            isCommand = true;
                            new Copy().preparingForRecursion(cmdBuffer[2], cmdBuffer[3]);
                        }
                    }
                }
            }
            if (cmdBuffer[0].equals("mv") && cmdBuffer.length == 3) {
                isCommand = true;
                new Move().moveFunction(cmdBuffer[1], cmdBuffer[2]);
            }
            if (cmdBuffer[0].equals("ls")) {
                isCommand = true;
                new ShowDirectoryContent().showDirectoryContentFunction();
            }
            if (cmdBuffer[0].equals("exit")) {
                isCommand = true;
                new Exit().exitFunction();
            }
            if (cmdBuffer[0].equals("pwd")) {
                isCommand = true;
                new ShowPath().showPathFunction();
            }
            if (cmdBuffer[0].equals("cat") && cmdBuffer.length == 2) {
                isCommand = true;
                new ShowFileContent().showFileContentFunction(cmdBuffer[1]);
            }
            if (!isCommand) {
                syntaxError();
            }
            isCommand = false;
        }
    }
    void syntaxError() {
        System.err.println("incorrect syntax");
    }
}

