package ru.fizteh.fivt.students.SibgatullinDamir.junit;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class PutCommand implements Commands {
    public void execute(String[] args, FileMap fileMap) throws MyException {
        if (args.length > 3) {
            throw new MyException("put: too many arguments");
        }
        if (args.length < 3) {
            throw new MyException("put: not enough arguments");
        }

        String key = args[1];
        String value = args[2];

        if (fileMap.containsKey(key)) {
            System.out.println("overwrite\n" + fileMap.get(key));
        } else {
            System.out.println("new");
        }
        fileMap.put(key, value);
        fileMap.changedKeys.add(key);
    }

    public String getName() {
        return "put";
    }
}
