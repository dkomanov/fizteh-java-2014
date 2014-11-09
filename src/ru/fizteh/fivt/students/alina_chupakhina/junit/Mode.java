package ru.fizteh.fivt.students.alina_chupakhina.junit;

import com.sun.javaws.exceptions.ExitException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Mode {
    public PrintStream out;
    public InputStream in;
    public static final String WELLCOME = "$ ";
    public Mode(InputStream is, PrintStream ps) {
        out = ps;
        in = is;
    }
    Mode() {
        out = System.out;
        in = System.in;
    }
    public void interactive() throws ExitException {
        out.print(WELLCOME);
        Scanner sc = new Scanner(in);
        while (true) {
            try {

                String[] s = sc.nextLine().trim().split(";");
                for (String command : s) {
                    Interpreter i = new Interpreter(out);
                    i.doCommand(command);
                }
            } catch (Exception e) {
                out.println(e.getMessage());

            }
            out.print(WELLCOME);
            if (!out.equals(System.out)) {
                throw new ExitException("exit", new Exception("test"), 0);
            }
        }
    }

    public void batch(final String[] args) throws Exception {
        String arg;
        if (args.length > 0) {
            arg = args[0];
            for (int i = 1; i != args.length; i++) {
                arg = arg + ' ' + args[i];
            }
            String[] commands = arg.trim().split(";");
            for (int i = 0; i != commands.length; i++) {
                Interpreter ir = new Interpreter(out);
                ir.doCommand(commands[i]);
            }
        }
    }
}
