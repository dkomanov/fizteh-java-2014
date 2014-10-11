package ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell;

public class ShellState {
    private  String currentDirectory;
    public ShellState(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
    public String getCurDir() {
        return currentDirectory;
    }      
    void changeCurDir(String newCurDir) {
        currentDirectory = newCurDir;
    }
}