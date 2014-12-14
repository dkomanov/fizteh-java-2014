package ru.fizteh.fivt.students.SibgatullinDamir.FileMap;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class ListCommand implements Commands {
    public void execute(String[] args, FileMap fileMap) throws MyException {
        if (args.length > 1) {
            throw new MyException("list: too many arguments");
        }

        int i = 0;
        for (String string : fileMap.keySet()) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(string);
            ++i;
        }
        System.out.println();
    }

    public String getName() {
        return "list";
    }
}
