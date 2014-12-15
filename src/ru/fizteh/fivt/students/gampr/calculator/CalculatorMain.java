package ru.fizteh.fivt.students.gampr.calculator;

public class CalculatorMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("You should run program with 1 argue, which is arithmetic expression");
            System.exit(1);
        }
        Calculator calc = new Calculator(args[0]);
        System.out.format("%f\n", (calc.calc()));
    }
}
