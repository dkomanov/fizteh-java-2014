package ru.fizteh.fivt.students.pavel_voropaev.filemap;

public final class Get {
    private static int argNum = 2;
    private static String name = "get";
    private static String usage = "Usage: put <key>";

    public static void exec(Database db, String[] param) throws IllegalArgumentException, IllegalStateException {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        if (param.length < argNum) {
            ThrowExc.notEnoughArg(name, usage);
        }
        
        String tmp = db.get(param[1]);
        if (tmp == null) {
            System.out.println("Not found");
        } else {
            System.out.println(tmp);
        }
    }
    

}
