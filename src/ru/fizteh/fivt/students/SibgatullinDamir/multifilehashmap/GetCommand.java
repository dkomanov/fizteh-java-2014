package ru.fizteh.fivt.students.SibgatullinDamir.multifilehashmap;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class GetCommand implements Commands {
    public void execute(String[] args, FileMap fileMap) throws MyException {
        if (args.length > 2) {
            throw new MyException("get: too many arguments");
        }
        if (args.length < 2) {
            throw new MyException("get: not enough arguments");
        }

        String key = args[1];

        if (fileMap.containsKey(key)) {
            System.out.println("found\n" + fileMap.get(key));
        } else {
            System.out.println("not found");
        }
    }

    public String getName() {
        return "get";
    }
}
