package ru.fizteh.fivt.students.andrey_reshetnikov.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class InteractiveInput extends Input{

	protected BufferedReader inputStream;

    public InteractiveInput() {
        commandBuffer = new LinkedList<>();
        inputStream = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public String[] nextCommand() throws IOException, CommandsIsEmpty {
        if (commandBuffer.isEmpty()) {
            System.out.print("$ ");
            String newLine = inputStream.readLine();
            if (newLine == null) {
                throw new CommandsIsEmpty();
            } else {
                String[] splittedInput = newLine.split(";");
                for (String i : splittedInput) {
                    commandBuffer.offer(parse(i));
                }
            }
        }
        return commandBuffer.remove();
    }
}
