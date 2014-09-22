package ru.fizteh.fivt.students.maxim_rep.shell.commands;

public class pwd implements shellCommand {

	private String CurrentPath;

	public pwd(String CurrentPath) {
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
