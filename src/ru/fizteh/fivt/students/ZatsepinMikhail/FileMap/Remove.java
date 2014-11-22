package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

public class Remove extends CommandFileMap {
    public Remove() {
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
        myFileMap.load(null);
        return true;
    }
}
