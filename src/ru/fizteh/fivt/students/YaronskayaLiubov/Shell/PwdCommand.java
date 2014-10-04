package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

public class PwdCommand extends Command {
    PwdCommand() {
        name = "pwd";
    }

    boolean execute(String[] args) {
        try {
            System.out.println(Shell.curDir.getCanonicalPath());
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }
}
