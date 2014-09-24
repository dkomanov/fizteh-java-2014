import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by КОМПЯШКА on 24.09.2014.
 */
public class Calculator {
    String expression;
    enum Lexeme {Plus, Minus, Multiplication, Division, Opening_brackets, Closing_brackets, Number, End}
    Lexeme currentlexeme;
    int position = 0;
    BigDecimal currentvalue;
    boolean waitingnumber = false;

    private void getLexeme() {
        if (position >= expression.length()) {
            currentlexeme = Lexeme.End;
            return;
        }
        char ch = expression.charAt(position);
        if (Character.isWhitespace(ch)) {
            position++;
            getLexeme();
            return;
        }

        if ((ch >= '0' && ch <= '9') || ch == '.') {
            boolean pointweredetected = false;
            StringBuilder value = new StringBuilder("");
            while (position < expression.length() && ((ch >= '0' && ch <= '9') || ch == '.'))
            {
                if (pointweredetected && ch == '.')  {
                    System.err.println("Syntax Error: detected incorrect symbol '.'");
                    System.exit(2);
                }
                if (ch == '.') {
                    pointweredetected = true;
                }
                value.append(ch);
                position++;
                if (position < expression.length()) {
                    ch = expression.charAt(position);
                }
            }
            currentvalue = new BigDecimal(value.toString());
            currentlexeme = Lexeme.Number;
            return;
        }

        switch (ch) {
            case '+':
                currentlexeme = Lexeme.Plus;
                position++;
                break;
            case '-':
                currentlexeme = Lexeme.Minus;
                position++;
                break;
            case '*':
                currentlexeme = Lexeme.Multiplication;
                position++;
                break;
            case '/':
                currentlexeme = Lexeme.Division;
                position++;
                break;
            case '(':
                currentlexeme = Lexeme.Opening_brackets;
                position++;
                break;
            case ')':
                currentlexeme = Lexeme.Closing_brackets;
                position++;
                break;
            default:
                System.err.println("Syntax Error: detected incorrect symbol '" + ch + "'");
                System.exit(2);
                break;
        }
    }

    private BigDecimal calcMultiplier() {
        BigDecimal result = new BigDecimal(0);
        if (!(currentlexeme.equals(Lexeme.Opening_brackets)
              || currentlexeme.equals(Lexeme.Minus)
              || currentlexeme.equals(Lexeme.Number))) {
            System.err.println("Syntax Error: expected '(' or '-' or Number on position " + (position + 1));
            System.exit(2);
        } else {
            if (currentlexeme.equals(Lexeme.Minus) && waitingnumber) {
                System.err.println("Syntax Error: can't be '-' after '*' or '/'");
                System.exit(2);
            }
            if (currentlexeme.equals(Lexeme.Minus)) {
                getLexeme();
                result = calcMultiplier().negate();
            }
            if (currentlexeme.equals(Lexeme.Number)) {
                result = currentvalue;
                getLexeme();
            }
            if (currentlexeme.equals(Lexeme.Opening_brackets)) {
                getLexeme();
                waitingnumber = false;
                result = calcExpression();
                if (!currentlexeme.equals(Lexeme.Closing_brackets)) {
                    System.err.println("Syntax Error: expected ')' on position " + (position + 1));
                    System.exit(2);
                }
                getLexeme();
            }
        }
        return result;
    }


    private BigDecimal calcSummand() {
        BigDecimal result = calcMultiplier();
        while (currentlexeme.equals(Lexeme.Multiplication) || currentlexeme.equals(Lexeme.Division)) {
            waitingnumber = true;
            try {
                Lexeme lastlexeme = currentlexeme;
                getLexeme();
                BigDecimal secondmultiplier = calcMultiplier();
                if (lastlexeme.equals(Lexeme.Multiplication)) {
                    result = result.multiply(secondmultiplier);
                } else {
                    if (secondmultiplier.equals(BigDecimal.ZERO)) {
                        System.err.println("Arithmetic Error: Division by zero");
                        System.exit(3);
                    }
                    result = result.divide(secondmultiplier, MathContext.DECIMAL128);
                }
            } catch (ArithmeticException e) {
                System.err.println("Arithmetic Error: can't divide or multiply");
                System.exit(3);
            }
            waitingnumber = false;
        }
        return result;
    }

    private BigDecimal calcExpression() {
        BigDecimal result = calcSummand();
        while (currentlexeme.equals(Lexeme.Plus) || currentlexeme.equals(Lexeme.Minus)) {
            try {
                if (currentlexeme.equals(Lexeme.Plus)) {
                    getLexeme();
                }
                BigDecimal secondsummand = calcSummand();
                result = result.add(secondsummand);
            } catch (ArithmeticException e) {
                System.err.println("Arithmetic Error: can't  or ");
                System.exit(3);
            }
        }
        return result;
    }

    private void solve(String[] args) {
        try {
            expression = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("No arguments");
            System.exit(1);
        }
        getLexeme();
        System.out.println(calcExpression());
    }

    public static void main(String[] args) {
        final Calculator myclass = new Calculator();
        myclass.solve(args);
    }
}
