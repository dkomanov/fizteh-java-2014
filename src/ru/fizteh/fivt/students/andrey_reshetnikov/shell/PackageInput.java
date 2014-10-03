package ru.fizteh.fivt.students.andrey_reshetnikov.shell;

import java.util.LinkedList;

public class PackageInput extends Input{
	public PackageInput(String[] in) {
		commandBuffer = new LinkedList<>();
		StringBuilder concatenetedInput = new StringBuilder();
		for (String i : in) {
			concatenetedInput.append(i);
			concatenetedInput.append(" ");
		}
		String[] splitInput = concatenetedInput.toString().split("; ");
		for (String i : splitInput) {
            commandBuffer.offer(parse(i));
        }
	}
	
	@Override
	public String[] nextCommand() throws CommandsIsEmpty {
		if (commandBuffer.isEmpty()) {
			throw new CommandsIsEmpty();
		}
		return commandBuffer.remove();
	}
	
	@Override
	public boolean isNext() {
		return !commandBuffer.isEmpty();
	}
}
