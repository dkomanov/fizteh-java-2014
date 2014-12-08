package ru.fizteh.fivt.students.SibgatullinDamir.junit;

/**
 * Created by Lenovo on 10.11.2014.
 */
public class SizeJUnitCommand implements Commands {
        public void execute(String[] args, FileMap fileMap) throws MyException {

            if (args.length == 1) {
                if (fileMap != null) {
                    System.out.println(fileMap.size());
                } else {
                    throw new MyException("no table");
                }
            } else {
                throw new MyException("size: too many arguments");
            }

        }

        public String getName() {
            return "size";
        }
}
