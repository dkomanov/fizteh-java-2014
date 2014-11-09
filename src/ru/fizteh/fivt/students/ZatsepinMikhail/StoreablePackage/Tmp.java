package ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage;

import java.text.ParseException;

public class Tmp {
    public static void main(String[] args) {
        String s = "<row><col>123</col><col>100</col></row>";
        try {
            Serializator.deserialize(s);
        } catch (ParseException e) {
            //
        }
    }
}
