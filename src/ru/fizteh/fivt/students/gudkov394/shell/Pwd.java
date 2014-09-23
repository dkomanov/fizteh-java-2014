package ru.fizteh.fivt.students.gudkov394.shell;

//проверено работает

public class Pwd {
    public Pwd(String[] currentArgs, CurrentDirectory cd) {
        if (currentArgs.length > 1) {
            System.err.println("extra arguments for pwd");
            System.exit(1);
        }
        System.out.println(cd.getCurrentDirectory());
    }
}
