package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

public class FmCommandSize extends CommandFileMap {
    public FmCommandSize() {
        name = "size";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        System.out.println(myFileMap.size());
        return true;
    }
}
