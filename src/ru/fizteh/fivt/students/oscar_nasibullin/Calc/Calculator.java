package ru.fizteh.fivt.students.oscar_nasibullin.Calc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Calculator {

    private static String expression;
    private static int    position = 0;
    private static char[] token;
    private static int    tokenType;

    private Calculator() {
        // checkstyle asked for this useless constructor. I don't understand java code conventions  
    }

    public static void main(final String[] args) {
        if (args.length > 1 || args.length == 0) {
            System.out.println("only one expression allowed");
            System.exit(0);
        }

        BigDecimal rezult = calculate(args[0]);
        System.out.println(rezult);
    }


    public static BigDecimal calculate(final String str) {

        expression = str;

        if (expression.matches("^[ -+*/()0-9]+")) {
            System.out.println("Syntax mistakes");
            System.exit(0);
        }

        getToken();
        return  additionOrSubstraction();
        }


    public static BigDecimal additionOrSubstraction() {
        char operator;
        BigDecimal temp;
        BigDecimal rezult = multOrDiv();

        try {
        	operator = token[0];
        	while (operator  == '+' || operator == '-') {
        		getToken();
        		temp = multOrDiv();
        		switch(operator) {
                	case '-':
                		rezult = rezult.subtract(temp);
                		break;
                	case '+':
                		rezult = rezult.add(temp);
                		break;
                	}
        		operator = token[0];
            	}
        	} catch (Throwable th) {
        		System.err.println(th);
        	}
        return rezult;
        }



    public static BigDecimal multOrDiv() {
        char operator;
        BigDecimal temp;
        BigDecimal rezult = unaryOperator();

        operator = token[0];
        while (operator == '*' || operator == '/') {
            getToken();
            temp = unaryOperator();
            switch(operator) {
                case '*':
                    rezult = rezult.multiply(temp);
                    break;
                case '/':
                    if (temp.compareTo(new BigDecimal(0.0)) == 0) {
                        System.out.println("Division by zero");
                        System.exit(0);
                        } else {
                        rezult = rezult.divide(temp, 2, RoundingMode.HALF_UP);
                    }
                    break;
            }
            operator = token[0];
        }
        return rezult;
        }


    public static BigDecimal unaryOperator() {
        char operator = 0;

        if ((tokenType == 1) && token[0] == '+' || token[0] == '-') {
            operator = token[0];
            getToken();
          }
        BigDecimal rezult = braces();
        if (operator == '-') {
            rezult = rezult.multiply(new BigDecimal(-1));
        }
        return rezult;
        }


    public static BigDecimal braces() {

        BigDecimal rezult;

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


    public static BigDecimal atom() {
      BigDecimal rezult = new BigDecimal(0.0);
      if (tokenType == 2) {
        rezult = new BigDecimal(String.valueOf(token));
        getToken();
        return rezult;
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
