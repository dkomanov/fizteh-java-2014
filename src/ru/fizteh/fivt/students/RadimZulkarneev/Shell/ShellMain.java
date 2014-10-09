package ru.fizteh.fivt.students.RadimZulkaneev.shell;

 
public final class ShellMain {
    public static void main(final String[] argc) {
        if (argc.length == 0) {
            Interactive.conv();
            
        } else {
            InputParse.myParse(argc);
        }
    }
    private ShellMain() {
    }
}
