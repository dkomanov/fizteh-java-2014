package ru.fizteh.fivt.students.maxim_rep.shell.commands;

public class emptyCommand implements shellCommand {

	public emptyCommand() {
	}

	@Override
	public boolean execute() {
		return true;
	}

}
