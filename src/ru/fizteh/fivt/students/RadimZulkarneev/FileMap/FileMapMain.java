package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;


public final class FileMapMain {

    private FileMapMain() {
        //
    }
    public static void main(final String[] arg) {

        if (arg.length > 0) {
            //Parse
            InputParse.parse(arg);
        } else {
            //Interactive
            Interactive.conv();
        }
    }
}
