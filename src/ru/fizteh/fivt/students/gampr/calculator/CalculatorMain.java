package ru.fizteh.fivt.students.gampr.calculator;

import java.io.IOException;

public class CalculatorMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("You should run program with 1 argue, which is arithmetic expression");
            System.exit(1);
        }
        Operation[] operations = {new OperationPlus(), new OperationMinus(), new OperationMult(),
                new OperationUnarMinus(), new OperationsDivision()};
        Calculator calc = new Calculator(args[0], operations);
        try {
            System.out.format("%f\n", (calc.calc()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
