package ru.fizteh.fivt.students.MaksimovAndrey.FileMap;


public class Exit extends Instruction {

    public Exit() {
        nameOfInstruction = "exit";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments, DataBase needbase) throws  Exception {
        if (arguments[0].equals("exit")) {
            System.exit(0);
        }
        return true;
    }
}
