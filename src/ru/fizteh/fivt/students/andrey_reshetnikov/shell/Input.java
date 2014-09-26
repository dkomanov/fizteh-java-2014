package ru.fizteh.fivt.students.andrey_reshetnikov.shell;

import java.io.IOException;
import java.util.Queue;

import ru.fizteh.fivt.students.andrey_reshetnikov.shell.Shell.StringParser;

public abstract class Input {
	
	Queue<String[]> commandBuffer;
	
	public abstract String[] nextCommand() throws IOException, CommandsIsEmpty;
	
	public boolean isNext() {
		return true;
	}
	
	public String[] parse(String string) {
			return string.split("\\s+");
	}	
}
