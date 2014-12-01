package ru.fizteh.fivt.students.MaksimovAndrey.shell;


public class Pwd extends Instruction {
    public Pwd() {
        nameOfInstruction = "pwd";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments) {
        System.out.println(presentDirectory.toString());
        return true;
    }
}
