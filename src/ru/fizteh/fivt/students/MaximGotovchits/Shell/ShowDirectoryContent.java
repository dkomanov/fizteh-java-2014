package fizteh.fivt.students.MaximGotovchits.Shell;

import java.io.File;

public class ShowDirectoryContent extends CommandTools {
    void showDirectoryContentFunction() {
        File tempFile = new File(currentDirectory);
        for (File sub : tempFile.listFiles()) {
            if (!sub.isHidden()) {
                System.out.println(sub.getName());
            }
        }
    }
}
