package ru.fizteh.fivt.students.anastasia_ermolaeva.filemap;

public final class FilemapMajor {
    private FilemapMajor() {
        //
    }

    public static void main(final String[] args) throws Exception {
        try (DbOperations db = new DbOperations()) {
            if (args.length == 0) {
                Filemap.userMode(db);
            } else {
                Filemap.batchMode(db, args);
            }
        } catch (ExitException t) {
            System.exit(t.getStatus());
        }
    }
}
