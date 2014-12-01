package ru.fizteh.fivt.students.deserg.junit;

/**
 * Created by deserg on 01.12.14.
 */
public class Shell {

    public static boolean checkName(String name) {

        return (!name.contains("\\") && !name.contains("/000"));

    }

}
