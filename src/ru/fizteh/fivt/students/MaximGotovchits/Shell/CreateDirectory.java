package fizteh.fivt.students.MaximGotovchits.Shell;

import java.io.File;

public class CreateDirectory extends CommandTools {
    void createDirectoryFunction(String dirName) {
        String absoluteDirName = currentDirectory + "/" + dirName;
        File directory = new File(absoluteDirName);
        if (!directory.exists()) {
            directory.mkdir();
        } else {
            System.err.println(dirName + " already exists");
        }
    }
}
