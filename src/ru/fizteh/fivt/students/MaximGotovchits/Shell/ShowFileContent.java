package fizteh.fivt.students.MaximGotovchits.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ShowFileContent extends CommandTools {
    void showFileContentFunction(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream stream = null;
        if (!file.isAbsolute()) {
            file = new File(currentDirectory + "/" + fileName);
        }
        if (file.isFile()) {
            File fl1 = new File(currentDirectory + "/" + fileName);
            stream = new FileInputStream(fl1);
            int content;
            while ((content = stream.read()) != -1) {
                System.out.print((char) content);
            }
        } else {
            System.err.println(file.getName() + " is a directory");
        }
        if (stream != null) {
            stream.close();
        }
    }
}
