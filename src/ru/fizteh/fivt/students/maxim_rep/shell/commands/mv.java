package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.parser;
import java.io.File;

public class mv implements shellCommand {

	String CurrentPath;
	String Source;
	String Destination;

	public mv(String CurrentPath, String Source, String Destination) {
		this.CurrentPath = CurrentPath;
		this.Source = parser.PathConverter(Source, CurrentPath);
		this.Destination = parser.PathConverter(Destination, CurrentPath);
	}

	@Override
	public boolean execute() {
		cp.copyDirectory(Source, Destination);
		run.recursiveRm((new File(Source)), Source);
		return true;
	}
}
