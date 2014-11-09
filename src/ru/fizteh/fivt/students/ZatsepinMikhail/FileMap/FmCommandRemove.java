package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

public class FmCommandRemove extends CommandFileMap {
    public FmCommandRemove() {
        name = "remove";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
       String value = myFileMap.remove(args[1]);

        if (myFileMap.) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
