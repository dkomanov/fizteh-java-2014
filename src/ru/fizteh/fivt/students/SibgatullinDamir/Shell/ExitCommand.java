package ru.fizteh.fivt.students.SibgatullinDamir.Shell;

/**
 * Created by Lenovo on 02.10.2014.
 */
public class ExitCommand implements Commands {
    public void execute(String[] args) throws MyException {

        if (args.length == 1) {
            System.exit(0);
        } else {
            throw new MyException("exit: too many arguments");
        }
    }

    public String getName() {
        return "exit";
    }
}
