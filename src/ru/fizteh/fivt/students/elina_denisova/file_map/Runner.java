package ru.fizteh.fivt.students.elina_denisova.file_map;

public class Runner {
    public static void main(String[] args) throws Exception {

        try {
            String path = System.getProperty("db.file");
            if (path == null) {
                throw new NullPointerException("Runner: File doesn't exist");
            }
            DataBase base = new DataBase(path);

            if (args.length == 0) {
                    InteractiveParse.parse(base);
                } else {
                    PackageParse.parse(base, args);
                }
            } catch (NullPointerException e) {
                HandlerException.handler(e);
            } catch (Exception e) {
                HandlerException.handler("Runner: Unknown error", e);
            }
    }
}

