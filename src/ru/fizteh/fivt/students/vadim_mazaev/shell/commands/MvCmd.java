package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class MvCmd {
    private MvCmd() {
        //not called
    }
    public static void run(final String[] cmdWithArgs) throws Exception {
        if (cmdWithArgs.length <= 2) {
            throw new Exception(getName() + ": missing operand");
        } else if (cmdWithArgs.length > 3) {
            throw new Exception(getName()
                    + ": too much arguments");
        }
        try {
            File movedFile = Paths.get(cmdWithArgs[1])
                            .normalize().toFile();
            if (!movedFile.isAbsolute()) {
                movedFile = Paths.get(System.getProperty("user.dir"),
                            cmdWithArgs[1]).normalize().toFile();
            }
            if (cmdWithArgs[1].isEmpty() || !movedFile.exists()) {
                throw new Exception(getName() + ": "
                    + cmdWithArgs[1] + ": No such file or directory");
            }
            File destinationFile = Paths.get(cmdWithArgs[1])
                                .normalize().toFile();
            if (!destinationFile.isAbsolute()) {
                destinationFile = Paths.get(System.getProperty("user.dir"),
                            cmdWithArgs[2]).normalize().toFile();
            }
            if (cmdWithArgs[2].isEmpty()) {
                throw new Exception(getName()
                        + ": cannot move '"
                        + cmdWithArgs[1]
                        + "' to '"
                        + cmdWithArgs[2]
                        + "': No such file or directory");
            }
            if (movedFile.isFile()) {
                if (!destinationFile.getParentFile().exists()) {
                    throw new Exception(getName() + ": cannot create '"
                            + cmdWithArgs[2]
                            + "': No such file or directory");
                }
                if (movedFile.getParent().equals(destinationFile.getParent())
                        && destinationFile.isFile()) {
                    if ((!destinationFile.exists() || destinationFile.delete())
                            && movedFile.renameTo(destinationFile)) {
                        throw new Exception(getName() + ": "
                                + "cannot rename file '"
                                + cmdWithArgs[1] + "' to '"
                                + cmdWithArgs[2] + "': smth went wrong");
                    }
                }
                if (destinationFile.isDirectory()) {
                    destinationFile = Paths.get(destinationFile.getPath(),
                                movedFile.getName()).toFile();
                }
                Files.move(movedFile.toPath(), destinationFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } else {
                if (destinationFile.isDirectory()) {
                    destinationFile = Paths.get(destinationFile.getPath(),
                            movedFile.getName()).toFile();
                }
                if (!mvRec(movedFile, destinationFile)) {
                    throw new Exception(getName() + ": "
                        + "cannot move file '"
                        + cmdWithArgs[1] + "' to '"
                        + cmdWithArgs[2] + "': smth went wrong");
                }
            }
        } catch (IOException e) {
            throw new Exception(getName()
                    + ": cannot read or write files");
        } catch (InvalidPathException e) {
            throw new Exception(getName()
                    + ": cannot move file '"
                    + cmdWithArgs[1] + "' to '"
                    + cmdWithArgs[2] + "': illegal character in name");
        } catch (SecurityException e) {
            throw new Exception(getName()
                    + ": cannot move file '"
                    + cmdWithArgs[1] + "' to '"
                    + cmdWithArgs[2] + "': access denied");
        }
    }
    private static boolean mvRec(final File copied, final File destination)
                                        throws IOException {
        if (copied.isDirectory()) {
            destination.mkdir();
            for (File f : copied.listFiles()) {
                if (!mvRec(f, Paths.get(destination.getAbsolutePath(),
                        f.getName()).toFile())) {
                    return false;
                }
            }
            if (!copied.delete()) {
                return false;
            }
        } else {
            Files.move(copied.toPath(), destination.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
        }
        return true;
    }
    public static String getName() {
        return "mv";
    }
}
