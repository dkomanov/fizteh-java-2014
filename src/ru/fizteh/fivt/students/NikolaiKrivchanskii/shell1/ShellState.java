package ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1;

public class ShellState {
    private  String currentDirectory;
    public ShellState(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
    public String getCurDir() {
        return currentDirectory;
    }      
    public void changeCurDir(String newCurDir) {
        currentDirectory = newCurDir;
    }
}