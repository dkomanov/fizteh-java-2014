package ru.fizteh.fivt.students.ElinaDenisova.FileMap;

public class DataBaseRunner {

    public static void main(String[] arg) {

        if (arg.length > 0) {
            InputParse.parse(arg);
        } else {
            InteractiveParse.parse();
        }
    }
}
