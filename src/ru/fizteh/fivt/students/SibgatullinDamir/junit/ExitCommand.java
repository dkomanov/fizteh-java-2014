package ru.fizteh.fivt.students.SibgatullinDamir.junit;

/**
 * Created by Lenovo on 02.10.2014.
 */
public class ExitCommand implements CommandsForTables {
    public void execute(String[] args, MultiFileHashMap multiFileHashMap) throws MyException {

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
