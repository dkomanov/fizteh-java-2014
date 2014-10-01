package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.IOException;

public interface Command {
	public void execute(String[] args) throws IOException, Exception;
	
	public String name();
	
	public int minArgs();
	
	public int maxArgs();
}

