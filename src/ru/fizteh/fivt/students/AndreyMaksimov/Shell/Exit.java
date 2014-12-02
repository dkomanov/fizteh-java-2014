package ru.fizteh.fivt.students.MaksimovAndrey.shell;

public class Exit extends Instruction {
    public Exit() {
        nameOfInstruction = "exit";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments) {
        if (arguments[0].equals("exit")) {
            System.exit(0);
        }
        return true;
    }
}
