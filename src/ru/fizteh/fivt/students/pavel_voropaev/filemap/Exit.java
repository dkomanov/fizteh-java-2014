package ru.fizteh.fivt.students.pavel_voropaev.filemap;

public final class Exit {
    private static int argNum = 1;
    private static String name = "exit";
    private static String usage = "Usage: exit";

    public static void exec(Database db, String[] param) throws Exception {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        db.write();
    }
    
}
