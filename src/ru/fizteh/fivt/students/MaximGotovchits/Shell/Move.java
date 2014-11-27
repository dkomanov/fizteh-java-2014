package fizteh.fivt.students.MaximGotovchits.Shell;

import java.io.File;
import java.io.IOException;

public class Move extends CommandTools {
    void moveFunction(String source, String destination) throws IOException {
        File sourceFile = new File(source);
        File destinationFile = new File(destination);
        if (!sourceFile.isAbsolute()) {
            sourceFile = new File(currentDirectory + "/" + source);
        }
        if (!destinationFile.isAbsolute()) {
            destinationFile = new File(currentDirectory + "/" + destination);
        }
        if (sourceFile.getParent().equals(destinationFile.getParent())) { // Переименование согласно условию задачи.
            String[] tempContent = sourceFile.list();
            destinationFile.renameTo(sourceFile);
            new Remove().removeRecursivelyFunction(sourceFile.getAbsolutePath());
        } else {
            new Copy().copyRecursivelyFunction(sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath());
            new Remove().removeRecursivelyFunction(sourceFile.getAbsolutePath());
        }
    }
}
