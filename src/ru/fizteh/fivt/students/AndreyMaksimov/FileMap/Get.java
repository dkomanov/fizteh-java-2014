package ru.fizteh.fivt.students.MaksimovAndrey.FileMap;


public class Get extends Instruction {

    public Get() {
        nameOfInstruction = "get";
    }

    @Override
    public boolean startNeedInstruction(String[] arguments, DataBase needbase) throws  Exception {
        if (arguments.length != 2) {
            System.out.println("ERROR: Missing operand");
            System.exit(1);
        }

        String value = needbase.needdatabase.get(arguments[1]); {
            if(value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        }
        return true;
    }
}

