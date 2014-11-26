package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.File;

class RemoveCommand extends ExternalCommand {
    public RemoveCommand() {
        super("rm", 1, 2);
    }

    private void recursiveRemove(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            for (File f: files) {
                if (f.isDirectory())  {
                    recursiveRemove(f);
                } else {
                    if (!f.delete()) {
                        throw new IllegalArgumentException("rm: unable to delete file: " + f.getName() + ".");
                    }
                }
            }
        }

        if (!file.delete()) {
            throw new IllegalArgumentException("rm: unable to delete file: " + file.getName() + ".");
        }
    }

    public void execute(String[] args, Shell shell) {
        int offset = 0;
        if (args[0].equals("-r")) {
            offset = 1;
        }

        if ((offset == 0 && args.length != 1) || (offset == 1 && args.length != 2)) {
            throw new IllegalArgumentException("rm: invalid arguments");
        }

        File target = Utilities.getAbsoluteFile(args[offset], shell.getCurrentPath());

        if (!target.exists()) {
            throw new IllegalArgumentException("rm: target file / directory doesn't exist.");
        }

        if (target.isDirectory() && offset == 0) {
            throw new IllegalArgumentException("rm: unable to remove directory without -r flag.");
        }

        recursiveRemove(target);
    }
}
