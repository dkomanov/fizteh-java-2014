package ru.fizteh.fivt.students.gudkov394.MultiMap;

import ru.fizteh.fivt.students.gudkov394.shell.CurrentDirectory;
import ru.fizteh.fivt.students.gudkov394.shell.RemoveDirectory;
import sun.plugin.PluginURLJarFileCallBack;

import javax.print.attribute.standard.MediaPrintableArea;
import java.io.File;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CurrentTable {
    String name;
    Map currentTable = new HashMap<String, String>();
    private int number = 0;

    public CurrentTable(String nameTmp) {
        name = nameTmp;
        create();
    }

    public CurrentTable() {
        name = null;
    }

    public String getName() {
        return name;
    }

    public String getHomeDirectory() {
        return System.getProperty("db.file" + File.separator + getName());
    }

    void write() {
        if (System.getProperty("db.file" + File.separator + getName()) == null) {
            System.err.println("You forgot file");
            System.exit(4);
        }
        File f = new File(System.getProperty("db.file" + File.separator + getName()));
        Write w = new Write(this, f);
    }

    public Set<String> keySet() {
        return currentTable.keySet();
    }

    public String get(String s) {
        return String.valueOf(currentTable.get(s));
    }

    public void put(String key, String value) {
        ++number;
        currentTable.put(key, value);
    }

    public boolean containsKey(String currentArg) {
        return currentTable.containsKey(currentArg);
    }

    public Object remove(String currentArg) {
        --number;
        return currentTable.remove(currentArg);
    }

    public void create() {
        File f = new File(getHomeDirectory());
        if (f.exists()) {
            System.out.println("tablename exists");
        } else if (!f.mkdirs()) {
            System.err.println("I can't create the directory");
            System.exit(1);
        } else {
            System.out.println("created");
        }
    }
  ////проверь что всё нормально с путями
    public void delete(){
        String[] s = new String[] {"remove", "-r", getName()};
        CurrentDirectory cd = new CurrentDirectory();
        cd.changeCurrentDirectory(System.getProperty("db.file"));
        RemoveDirectory removeDirectory = new RemoveDirectory(s, cd);
        System.out.println("Deleted");
    }

    public int getNumber() {
        return number;
    }
}
