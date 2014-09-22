package ru.fizteh.fivt.students.maxim_rep.shell.commands;

public class unknownCommand implements shellCommand {

	private String args;

	public unknownCommand(String args) {
		this.args = args;
	}

	@Override
	public boolean execute() {
		System.out.println(args + ": Command not found");
		return false;
	}

}
