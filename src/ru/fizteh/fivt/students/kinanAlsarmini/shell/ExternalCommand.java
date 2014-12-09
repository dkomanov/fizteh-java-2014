package ru.fizteh.fivt.students.kinanAlsarmini.shell;

import java.util.ArrayList;

public abstract class ExternalCommand {
    private String name;
    private ArrayList<Integer> argNumbers;

    public ExternalCommand(String name, int... argNumbers) {
        this.name = name;

        this.argNumbers = new ArrayList<Integer>();
        for (int i = 0; i < argNumbers.length; i++) {
            this.argNumbers.add(argNumbers[i]);
        }
    }

    public String getName() {
        return name;
    }

    public boolean checkArgNumber(int argNumber) {
        return argNumbers.contains(argNumber);
    }

    public abstract void execute(String[] args, Shell shell);
}
