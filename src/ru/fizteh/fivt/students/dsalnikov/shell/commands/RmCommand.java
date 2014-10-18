package ru.fizteh.fivt.students.dsalnikov.shell.commands;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;

public class RmCommand implements Command {

    private Shell link;

    public RmCommand(Shell s) {
        link = s;
    }

    public String getName() {
        return "rm";
    }

    public int getArgsCount() {
        return 1;
    }

    private void delete(File f) throws IOException {
        if (f.isDirectory()) {
            //если размер директории 0 можем просто удалить ее
            //если нет то перебираем все файлы внутри и вызываем удаление рекурсивно
            if (f.list().length == 0) {
                deleteFile(f);
            } else {
                String[] files = f.list();
                for (String s : files) {
                    File fileDelete = new File(f, s);
                    delete(fileDelete);
                }
                if (f.list().length == 0) {
                    deleteFile(f);
                }
            }
        } else {
            deleteFile(f);

        }
    }

    private void deleteFile(File f) throws IOException {
        if (!f.delete()) {
            throw new IOException("Something went wrong. File wasn't deleted. Try moar");
        }
    }

    public void execute(String[] s) throws IOException {
        if (s.length != 2 && s.length != 3) {
            throw new IllegalArgumentException("wrong ammount of args. should be called with one arg");
        } else if (s.length == 2) {
            File f = StringUtils.processFile(link.getState().getState(), s[1]);
            if (f.isDirectory() && f.list().length != 0) {
                throw new DirectoryNotEmptyException("Directory isn't empty. Use -r flag");
            } else {
                delete(f);
            }
        } else {
            File f = StringUtils.processFile(link.getState().getState(), s[2]);
            if (s[1].equals("-r")) {
                delete(StringUtils.processFile(link.getState().getState(), s[2]));
            } else {
                throw new IllegalArgumentException("rm command: " + s[2] + "flag not supported");
            }
        }
    }
}
