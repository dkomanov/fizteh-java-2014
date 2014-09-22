package ru.fizteh.fivt.students.maxim_rep.shell.commands;

public class Pwd implements ShellCommand {

	private String CurrentPath;

	public Pwd(String CurrentPath) {
		this.CurrentPath = CurrentPath;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean execute() {
		System.out.println(CurrentPath);
		return true;
	}

}
