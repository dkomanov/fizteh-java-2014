package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

public class Exit extends CommandTools {
    void exitFunction() {
        System.exit(0);
    }
    Boolean exitAvailable() {
        uncommitedChanges = Math.abs(storage.size() - commitStorage.size());
        if (uncommitedChanges == 0) {
            return true;
        }
        System.out.println(uncommitedChanges + " uncommited changes");
        return false;
    }
}


