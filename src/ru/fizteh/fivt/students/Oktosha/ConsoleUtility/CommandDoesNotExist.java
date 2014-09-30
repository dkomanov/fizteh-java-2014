package ru.fizteh.fivt.students.Oktosha.ConsoleUtility;

/**
 * Created by DKolodzey on 30.09.14.
 */
public class CommandDoesNotExist extends ConsoleUtilityException {
    CommandDoesNotExist(String s) {
        super(s);
    }
}
