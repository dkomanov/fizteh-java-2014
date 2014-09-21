package shell.commands;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cat implements ShellCommand {

    String CurrentPath;
    String FileName;

    public Cat(String CurrentPath, String FileName) {
        this.FileName = shell.Parser.PathConverter(FileName, CurrentPath);
        this.CurrentPath = CurrentPath;
    }

    @Override
    public boolean execute() {
        File f = new File(FileName);

        if (!f.exists() || !f.isFile()) {
            System.out.println("cat: " + FileName + ": No such file");
            return true;
        } else {
            FileReader in;
            try {
                in = new FileReader(f);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Cat.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }

            char[] buffer = new char[4096];
            int len;
            try {
                while ((len = in.read(buffer)) != -1) {
                    String s = new String(buffer, 0, len);
                    System.out.println(s);
                }
            } catch (IOException ex) {
                Logger.getLogger(Cat.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Cat.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            }

        }
        return true;
    }
}
