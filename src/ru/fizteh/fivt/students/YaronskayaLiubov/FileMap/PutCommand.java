package ru.fizteh.fivt.students.YaronskayaLiubov.FileMap;

/**
 * Created by luba_yaronskaya on 06.10.14.
 */
public class PutCommand extends Command {
    PutCommand() {
        name = "put";
        numberOfArguements = 3;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        String old = FileMap.data.put(args[1], args[2]);
        if (old != null) {
            System.out.println("overwrite");
            System.out.println(old);
        } else {System.out.println("new");}
        return true;
    }
}
