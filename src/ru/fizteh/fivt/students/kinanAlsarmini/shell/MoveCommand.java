package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.io.IOException;
import java.io.File;

class MoveCommand extends ExternalCommand {
    public MoveCommand() {
        super("mv", 2);
    }

    public void execute(String[] args, Shell shell) {
        File source = Utilities.getAbsoluteFile(args[0], shell.getCurrentPath());
        File destination = Utilities.getAbsoluteFile(args[1], shell.getCurrentPath());

        if (!source.exists()) {
            throw new IllegalArgumentException("mv: source doesn't exist.");
        }

        if (destination.isDirectory()) {
            RemoveCommand rc = new RemoveCommand();
            CopyCommand cc = new CopyCommand();

            cc.execute(args, shell);
            rc.execute(new String[] {args[0]}, shell);
        } else {
            try {
                String sourceS = source.getParentFile().getCanonicalPath();
                String destinationS = destination.getParentFile().getCanonicalPath();
                if (sourceS.equals(destinationS)) {
                    if (!source.renameTo(destination)) {
                        throw new IllegalArgumentException("mv: unable to rename "
                                + "source to destination.");
                    }
                } else {
                    throw new IllegalArgumentException("mv: destination isn't a directory.");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("mv: can't read parents' directories!");
            }
        }
    }
}
