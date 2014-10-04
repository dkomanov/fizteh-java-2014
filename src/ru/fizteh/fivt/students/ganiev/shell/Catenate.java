package ru.fizteh.fivt.students.ganiev.shell;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Catenate implements Command {
    public void invokeCommand(String[] arguments, Shell.MyShell shell) throws IOException {
        File myFile = FileCommander.getFile(arguments[0], shell);

        if ((myFile.exists()) && (myFile.isFile()) && (myFile.canRead())) {
            try (BufferedReader br = new BufferedReader(new FileReader(myFile.getPath())))
            {
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) {
                    System.out.println(sCurrentLine);
		}
            } catch (IOException exception){
                throw new IOException("cat: " + myFile.getPath() + ": No such file or directory");
            }
        }
    }
    public int getNumberOfArguments() {
        return 1;
    }
}
