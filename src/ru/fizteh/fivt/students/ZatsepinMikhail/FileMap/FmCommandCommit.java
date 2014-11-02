package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

public class FmCommandCommit extends CommandFileMap {
    public FmCommandCommit() {
        name = "commit";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        int result = myFileMap.commit();
        if (result == -1) {
            System.err.println("error ocquired while load");
            return false;
        } else {
            System.out.println(result);
            return true;
        }
    }
}
