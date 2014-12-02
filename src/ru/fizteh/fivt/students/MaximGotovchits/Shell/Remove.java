package fizteh.fivt.students.MaximGotovchits.Shell;

import java.io.File;

public class Remove extends CommandTools {
    void removeFunction(String fileName) {
        File tempFile = new File(fileName);
        if (!tempFile.isAbsolute()) {
            tempFile = new File(currentDirectory + "/" + fileName);
        }
        if (tempFile.exists()) {
            if (tempFile.list().length == 0) {
                tempFile.delete();
            } else {
                System.err.println(tempFile.getName() + " is not empty. Try use '-r' parameter");
            }
        }  else {
            System.err.println(tempFile.getName() + " doesn't exist");
        }
    }
    void removeRecursivelyFunction(String fileName) {
        File tempFile = new File(fileName);
        if (!tempFile.isAbsolute()) {
            tempFile = new File(currentDirectory + "/" + fileName);
        }
        if (tempFile.exists()) {
            recursion(tempFile);
        } else {
            System.err.println(tempFile.getName() + " doesn't exist");
        }
    }
    void recursion(File tempFile) {
        for (String file: tempFile.list()) {
            recursion(new File(tempFile.getAbsolutePath() + "/" + file));
        }
        tempFile.delete();
    }
}
