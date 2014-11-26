package fizteh.fivt.students.MaximGotovchits.Shell;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class InteractiveMode {
    String [] cmdBuffer = new String[256];
    Boolean isCommand = false;
    Scanner sc = new Scanner(System.in);
    String cmd = "";
    String tableName = new String();
    static Map<String, String> storage = new HashMap<String, String>();
    void interactiveModeFunction() throws Exception {
        while (true) {
            System.out.print("$ ");
            cmd = sc.nextLine();
            cmd = cmd.replaceAll("\\s+", " ");
            if (cmd.equals("") || cmd.equals(" ")) {
                continue;
            }
            cmdBuffer = cmd.split(" ");
            if (cmdBuffer[0].equals("") && cmdBuffer.length > 0) {
                cmd = "";
                for (int ind = 1; ind < cmdBuffer.length; ++ind) {
                    cmd = cmd + cmdBuffer[ind] + " ";
                }
                cmdBuffer = cmd.split(" ");
            }
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
                sc.close();
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
