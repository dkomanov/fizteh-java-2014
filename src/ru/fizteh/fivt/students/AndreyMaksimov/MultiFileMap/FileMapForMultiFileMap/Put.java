package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap;

public class Put extends Instruction {

    public Put() {
        nameOfInstruction = "put";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments, DataBase needBase) throws Exception {
        if (arguments.length != 3) {
            System.out.println("Missing operand");
            System.exit(1);
        }
        String value = needBase.needdatabase.get(arguments[1]);
        if (value != null) {
            System.out.println("overwrite");
            System.out.println(value);
        } else {
            System.out.println("new");
        }

        needBase.needdatabase.put(arguments[1], arguments[2]);
        needBase.rewrite();
        return true;
    }
}
