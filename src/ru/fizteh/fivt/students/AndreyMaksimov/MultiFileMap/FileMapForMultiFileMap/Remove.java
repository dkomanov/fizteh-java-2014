package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap;

public class Remove extends Instruction {
    public Remove() {
        nameOfInstruction = "remove";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments, DataBase needBase) throws Exception {
        if (arguments.length != 2) {
            System.out.println("ERROR: Missing operand");
            System.exit(1);
        }

        if (needBase.needdatabase.containsKey(arguments[1])) {
            needBase.needdatabase.remove(arguments[1]);
            needBase.rewrite();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
