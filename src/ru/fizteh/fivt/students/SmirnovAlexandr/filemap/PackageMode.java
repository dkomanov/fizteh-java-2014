package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public class PackageMode implements CommandProcess {

    public String[] commands;
    public PackageMode(String[] args) {
        StringBuilder s1 = new StringBuilder();
        for (String s : args) {
            s1.append(s);
            s1.append(' ');
        }
        commands = s1.toString().split(";\\s*");
    }

    @Override
    public void process(Container commandFromString) throws ExceptionUnknownCommand {
        for (String s : commands) {
            boolean flag = true;
            try {
                commandFromString.getCommandByName(s).execute();
            } catch (ExceptionStopProcess | ExceptionIncorrectInput e) {
                flag = false;
            }
            if (!flag) {
                break;
            }
        }
    }
}
