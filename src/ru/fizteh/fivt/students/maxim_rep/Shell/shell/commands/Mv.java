package shell.commands;

import java.io.File;

public class Mv implements ShellCommand {

    String CurrentPath;
    String Source;
    String Destination;

    public Mv(String CurrentPath, String Source, String Destination) {
        this.CurrentPath = CurrentPath;
        this.Source = shell.Parser.PathConverter(Source, CurrentPath);
        this.Destination = shell.Parser.PathConverter(Destination, CurrentPath);
    }
    
    @Override
    public boolean execute() {
    	Cp.copyDirectory(Source, Destination);
    	Rm.recursiveRm((new File(Source)), Source);
    	return true;
    }
}
