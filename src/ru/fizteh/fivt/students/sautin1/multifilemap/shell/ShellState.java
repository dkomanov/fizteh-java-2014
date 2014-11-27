package ru.fizteh.fivt.students.sautin1.multifilemap.shell;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class stores data which must be accessed by both shell and commands.
 * Created by sautin1 on 10/4/14.
 */
public class ShellState {
    private Path pwd;

    public ShellState() {
        pwd = Paths.get("").toAbsolutePath().normalize();
    }

    /**
     * Getter of pwd field.
     * @return Path to the current working directory.
     */
    public Path getPWD() {
        return pwd;
    }

    /**
     * Setter of pwd field.
     * @param presentWorkingDirectory - new path for current working directory.
     */
    public void setPWD(Path presentWorkingDirectory) {
        this.pwd = presentWorkingDirectory;
    }

}
