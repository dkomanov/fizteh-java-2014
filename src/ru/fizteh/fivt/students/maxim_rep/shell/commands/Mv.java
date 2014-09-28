package ru.fizteh.fivt.students.maxim_rep.shell.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.Parser;
import java.io.File;

public class Mv implements ShellCommand {

String currentPath;
String source;
String destination;

	public Mv(String currentPath, String source, String destination) {
		this.currentPath = currentPath;
		this.source = Parser.pathConverter(source, currentPath);
		this.destination = Parser.pathConverter(destination, currentPath);
	}

	@Override
	public boolean execute() {
		Cp.copyDirectory(source, destination);
		Rm.recursiveRm((new File(source)), source);
		return true;
	}
}
