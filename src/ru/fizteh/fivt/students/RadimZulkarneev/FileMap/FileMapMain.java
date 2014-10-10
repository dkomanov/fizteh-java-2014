package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;


final public class FileMapMain {

    private FileMapMain() {
        //
    }
    public static void main(final String[] arg) {

        if (arg.length > 0) {
            //Parse
            InputParse.Parse(arg);
        } else {
            //Interactive
            Interactive.conv();
        }
    }
}
