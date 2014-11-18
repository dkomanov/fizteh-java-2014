package ru.fizteh.fivt.students.gudkov394.MultiMap;

import ru.fizteh.fivt.students.gudkov394.map.*;
import ru.fizteh.fivt.students.gudkov394.shell.CurrentDirectory;
import ru.fizteh.fivt.students.gudkov394.shell.RemoveDirectory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Created by kagudkov on 28.09.14.
 */
public class Write {

    public Write(final CurrentTable currentTable, File f) {
        clearDirectory(f);
        Set<String> set = currentTable.keySet();
        for (String s : set) {
            int hashcode = s.hashCode();
            int ndirectory = (hashcode + 16) % 16;
            int nfile = (hashcode / 16 + 16) % 16;

            FileOutputStream output = null;
            try {
                String dirName = ((Integer) ndirectory).toString();
                File newDir = new File(f + File.separator + dirName);
                if (!(newDir.exists())) {
                    if (!newDir.mkdirs()) {
                        System.err.println("I can't create directory");
                        System.exit(1);
                    }
                }
                String fileName = ((Integer) nfile).toString() + ".dat";
                String pathToFile = f + File.separator + dirName + File.separator + fileName;
                File newFile = new File(pathToFile);
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                output = new FileOutputStream(newFile, true);
            } catch (IOException e) {
                System.err.println("Ouput file didn't find");
                System.exit(2);
            }
            writeWord(s, output);
            String tmp = currentTable.get(s).toString();
            writeWord(tmp, output);
        }
    }


    private void clearDirectory(File f) {
        String[] filesInDirectory = f.list();
        if (filesInDirectory != null) {
            for (String tmp : filesInDirectory) {
                String[] arg = {"rm", "-r", tmp};
                CurrentDirectory cd = new CurrentDirectory();
                cd.changeCurrentDirectory(f.getPath());
                RemoveDirectory remove = new RemoveDirectory(arg, cd);
            }
        }
    }

    private void writeWord(final String s, final FileOutputStream output) {

//        String key = ((Integer) s.length()).toString();
  //      writeBytes(key, output);
        writeBytes(s, output);
    }

    private void writeBytes(final String key, final FileOutputStream output) {
        try {
            byte[] bytes = ByteBuffer.allocate(4).putInt(key.length()).array();
            output.write(bytes);
        } catch (NullPointerException e) {
            System.err.println("Problem with write");
            System.exit(5);
        } catch (IOException e) {
            System.err.println("Problem with write");
            System.exit(5);
        }
        try {
            output.write(key.getBytes("UTF-8"));
        } catch (IOException e2) {
            System.err.println("Problem with write key");
            System.exit(5);
        }
    }
}
