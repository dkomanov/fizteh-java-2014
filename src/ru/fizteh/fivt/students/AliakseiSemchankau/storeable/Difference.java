package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;


/**
 * Created by Aliaksei Semchankau on 20.10.2014.
 */
public class Difference {

    public static String difference(String subName, String name) {
        if (subName.length() + 1 > name.length()) {
            throw new DatabaseException("incorrect lengthes for function Difference: " + subName + ", " + name);
        }
        String dif = new String();
        for (int i = subName.length() + 1; i < name.length(); ++i) {
            dif += name.charAt(i);
        }

        return dif;
    }

}