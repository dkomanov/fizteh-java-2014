package ru.fizteh.fivt.students.ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

/**
 * Created by Hoderu on 09.10.14.
 */
public class BatchMode implements CommandProcess {

    public String[] commands;
    public BatchMode(String[] args) {
        StringBuilder s1 = new StringBuilder();
        for (String s : args) {
            s1.append(s);
            s1.append(' ');
        }
        commands = s1.toString().split(";");
    }

    @Override
    public void process(CommandContainer commandFromString) throws UnknownCommand {
        for (String s : commands) {
            boolean flag = true;
            try {
                commandFromString.getCommandByName(s).execute();
            } catch (StopProcess | IncorrectInputException e) {
                flag = false;
            }
            if (!flag) {
                break;
            }
        }
    }
}
