package ru.fizteh.fivt.students.standy66.shell;

import java.io.File;

/**
 * Created by astepanov on 21.09.14.
 */
public class MoveAction extends Action {
    public MoveAction(String[] args) {
        super(args);
    }

    //TODO: fix move
    @Override
    public boolean run() {
        File source = FileUtils.fromPath(arguments[1]);
        File dest = FileUtils.fromPath(arguments[2]);
        CopyAction copyAction = new CopyAction(new String[]
                {"cp", "-r", source.getAbsolutePath(), dest.getParentFile().getAbsolutePath()});
        RemoveAction removeAction = new RemoveAction(new String[]{"rm", "-r", arguments[1]});
        if (!copyAction.run()) {
            return false;
        }
        new File(dest.getParentFile(), source.getName()).renameTo(dest);
        if (!removeAction.run()) {
            return false;
        }
        return true;
    }
}
