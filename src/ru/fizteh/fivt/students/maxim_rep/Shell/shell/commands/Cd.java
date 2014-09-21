package shell.commands;

import java.io.File;
import java.io.IOException;

public class Cd implements ShellCommand {

    private String CurrentPath;
    private String Destination;

    public Cd(String CurrentPath, String Destination) {
        this.CurrentPath = CurrentPath;
        //this.Destination = Destination;
        this.Destination = shell.Parser.PathConverter(Destination, CurrentPath);
        System.out.println("DEBUG 3: " + this.CurrentPath + " AND " + this.Destination);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean execute() {
        File f = new File(Destination);

        if (!f.exists()) {
            System.out.println("cd: " + Destination + ": No such file or directory");
        } else {
            try {
				shell.Shell.CurrentPath = f.getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return true;
    }

}
