package DbManager;

import TableManager.TableManager;

import java.io.File;

public final class DbManager {
    private final String rootDirectory = System.getProperty("fizteh.db.dir");
    private final int maxDirsForTable = 16;
    private final int maxFilesForDir = 16;

    private File currentTable = null;
    private int currentDir = 0;
    private int currentFile = 0;


    public static void main(final String[] args) {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }

    private static void interactiveMode() {

    }

    private static void batchMode(final String[] args) {

    }
}
