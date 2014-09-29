package ru.fizteh.fivt.students.oscar_nasibullin.Calc;

public final class Calculator {

    private static String expression;
    private static int    position = 0;
    private static char[] token;
    private static int    tokenType;
    //static double rezult;

    public static void main(final String[] args) {
        if (args.length > 1 || args.length == 0) {
            System.out.println("only one expression allowed");
            System.exit(0);
        }

        double rezult = calculate(args[0]);
        System.out.println(rezult);
    }


    public static double calculate(final String str) {
        
        expression = str;

        if (expression.matches("[^ -+*/()0-9]+")) {  // Check syntax 
            System.out.println("Syntax mistakes");
            System.exit(0);
        }

        getToken();
        return  additionOrSubstraction();
        }


    public static double additionOrSubstraction() {
        char operator;
        double temp;
        double rezult = multOrDiv();

        operator = token[0];
        while (operator  == '+' || operator == '-') {
            getToken();
            temp = multOrDiv();
            switch(operator) {
                case '-':
                    rezult = rezult - temp;
                    break;
                case '+':
                    rezult = rezult + temp;
                    break;
                }
            operator = token[0];
            }
        return rezult;
        }



    public static double multOrDiv() {
        char operator;
        double temp;
        double rezult = unaryOperator();

        operator = token[0];
        while (operator == '*' || operator == '/') {
            getToken();
            temp = unaryOperator();
            switch(operator) {
                case '*':
                    rezult = rezult * temp;
                    break;
                case '/':
                    if (temp == 0.0) {
                        System.out.println("Division by zero");
                        System.exit(0);
                        } else {
                        rezult = rezult / temp;
                    }
                    break;
            }
            operator = token[0];
        }
        return rezult;
        }


    public static double unaryOperator() {
        char operator = 0;

        if ((tokenType == 1) && token[0] == '+' || token[0] == '-') {
            operator = token[0];
            getToken();
          }
        double rezult = braces();
        if (operator == '-') {
            rezult = -(rezult);
        }
        return rezult;
        }


    public static double braces() {

        double rezult;

        if ((token[0] == '(')) {
            getToken();
            rezult = additionOrSubstraction();
            if (token[0] != ')') {
                System.out.println("incorrect brace expression");
                System.exit(0);
            }
            getToken();
          } else {
             rezult = atom();
          }
        return rezult;
    }


    public static double atom() {
      Double rezult = 0.0;
      if (tokenType == 2) {
        String rez = String.valueOf(token);
        rezult = new Double(rez);
        getToken();
        return rezult.doubleValue();
      }
      System.out.println("Syntax error");
      System.exit(0);
      return rezult;
    }


    public static void getToken() {

        tokenType = 0; //End token
        String temp = new String();

        while (position < expression.length() && expression.charAt(position) == ' ') {
            position++;
        }

        if (position == expression.length()) {
            return;
        }


        char sym = expression.charAt(position);
        String symb = new String() + sym;

        if (symb.matches("[-+*/()]")) {
            tokenType = 1; // Delimiter
            token = new char[1];
            token[0] = expression.charAt(position);
            position++;

        } else {
            while (position < expression.length() && (Character.isDigit(expression.charAt(position)) || expression.charAt(position) == '.')) {
            temp += expression.charAt(position);
            position++;
            }
            tokenType = 2; // Number
            token = temp.toCharArray();
        }

    }

}
