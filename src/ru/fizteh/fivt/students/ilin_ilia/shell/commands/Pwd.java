package ru.fiztech.fivt.students.theronsg.shell.commands;

public final class Pwd {
    public static void run() {
        System.out.println(System.getProperty("user.dir"));
    }

}
