package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

public class Put extends CommandFileMap {
    public Put() {
        name = "put";
        numberOfArguments = 3;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        String oldValue = myFileMap.put(args[1], args[2]);
        if (oldValue != null) {
            System.out.println("overwrite\n" + oldValue);
            myFileMap.load(args[1], false);
        } else {
            System.out.println("new");
            myFileMap.load(args[1], true);
        }
        return true;
    }
}
