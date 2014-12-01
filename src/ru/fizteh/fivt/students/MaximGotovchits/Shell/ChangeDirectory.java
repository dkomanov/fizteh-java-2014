package fizteh.fivt.students.MaximGotovchits.Shell;

import java.io.File;

public class ChangeDirectory extends CommandTools {
    void changeDirectoryFunction(String dirName) {
        if (dirName.equals("")) {
            currentDirectory = new File(new File(currentDirectory).getParent()).getAbsolutePath();
            return;
        }
        File tempFile = new File(dirName);
        if (!tempFile.isAbsolute()) {
            tempFile = new File(currentDirectory + "/" + dirName);
        }
        if (tempFile.exists()) {
            currentDirectory = tempFile.getAbsolutePath();
        } else {
            System.err.println(tempFile.getName() + " doesn't exist");
        }
    }
}
