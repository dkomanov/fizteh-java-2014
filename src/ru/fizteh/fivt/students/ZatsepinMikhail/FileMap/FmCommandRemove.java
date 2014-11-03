package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

public class FmCommandRemove extends CommandFileMap {
    public FmCommandRemove() {
        name = "remove";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        String value = myFileMap.remove(args[1]);
        if (value != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
