package fizteh.fivt.students.MaximGotovchits.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Copy extends CommandTools {
    void copyFunction(String sourceFile, String destinationDirectory) throws IOException { // "sourceFile" не означает,
    // что это имя файла, но не директории. "sourceFile" также может оказаться директорией.
        File tempSourceFile = new File(sourceFile);
        File tempDestinationFile = new File(destinationDirectory);
        if (!tempDestinationFile.isAbsolute()) {
            tempDestinationFile = new File(currentDirectory + "/" + destinationDirectory);
        }
        if (!tempSourceFile.isAbsolute()) {
            tempSourceFile = new File(currentDirectory + "/" + sourceFile);
        }
        if (tempSourceFile.exists()) {
            if (tempSourceFile.list().length != 0) {
                System.err.println(tempSourceFile.getName() + " is not empty. Try use '-r' parameter");
                return;
            }
        } else {
            System.err.println(tempSourceFile.getName() + " doesn't exist");
            return;
        }
        if (!tempDestinationFile.isAbsolute()) {
            tempDestinationFile = new File(currentDirectory + "/" + destinationDirectory);
        }
        if (tempDestinationFile.exists()) {
            if (tempDestinationFile.isDirectory()) {
                File newFile = new File(destinationDirectory + "/" + tempSourceFile.getName());
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
            } else {
                System.err.println(tempDestinationFile.getName() + " is not a directory");
                return;
            }
        } else {
            System.err.println(tempDestinationFile.getName() + " doesn't exist");
            return;
        }
    }
    void preparingForRecursion(String source, String destination) throws IOException {
        File sourceFile = new File(source);
        File destinationFile = new File(destination);
        if (!destinationFile.isAbsolute()) {
            destinationFile = new File(currentDirectory + "/" + destination);
        }
        if (!sourceFile.isAbsolute()) {
            sourceFile = new File(currentDirectory + "/" + sourceFile);
        }
        copyRecursivelyFunction(sourceFile.getAbsolutePath(),
                destinationFile.getAbsolutePath() + "/" + sourceFile.getName());
    }
    void copyRecursivelyFunction(String source, String destination) throws IOException {
        File sourceFile = new File(source);
        File destinationFile = new File(destination);
        if (!destinationFile.isAbsolute()) {
            destinationFile = new File(currentDirectory + "/" + destination);
        }
        if (!sourceFile.isAbsolute()) {
            sourceFile = new File(currentDirectory + "/" + sourceFile);
        }
        if (sourceFile.isDirectory()) {
            if (!destinationFile.exists()) {
                destinationFile.mkdirs();
            }
            String[] fList = sourceFile.list();
            for (int index = 0; index < fList.length; index++) {
                File tempDstFile = new File(destinationFile, fList[index]);
                File tempSrcFile = new File(sourceFile, fList[index]);
                copyRecursivelyFunction(tempSrcFile.getAbsolutePath(), tempDstFile.getAbsolutePath());
            }
        } else {
            FileInputStream iStream = new FileInputStream(sourceFile);
            FileOutputStream oStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[4096];
            int iBytesReads;
            while ((iBytesReads = iStream.read(buffer)) >= 0) {
                oStream.write(buffer, 0, iBytesReads);
            }
            if (iStream != null) {
                oStream.close();
            }
            if (oStream != null) {
                oStream.close();
            }
        }
    }
}
