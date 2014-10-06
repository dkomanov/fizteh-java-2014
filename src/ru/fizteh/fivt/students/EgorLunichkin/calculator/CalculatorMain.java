import java.math.BigDecimal;
import java.util.Stack;
import java.util.StringTokenizer;

public class CalculatorMain
{
    public static void main (String[] args) throws CalculatorException
    {
        if (args.length < 1)
            throw new CalculatorException("Expression expected");
        try
        {
            BigDecimal result = calc(args[0]);
            System.out.println(result.toString());
        }
        catch (CalculatorException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static BigDecimal calc(String expression) throws CalculatorException
    {
        expression = modifyExpression(expression);
        final String operationSymbols = "+-*/()!";
        StringTokenizer tokenizer = new StringTokenizer(expression, operationSymbols, true);
        Stack<Operand> nums = new Stack<Operand>();
        Stack<Operator> ops = new Stack<Operator>();
        while (tokenizer.hasMoreTokens())
        {
            Element element = Element.parse(tokenizer.nextToken());
            element.pushElement(nums, ops);
        }
        if (!ops.isEmpty())
            throw new CalculatorException("Too many operators");
        if (nums.isEmpty())
            throw new CalculatorException("Too few numbers");
        if (nums.size() > 2)
            throw new CalculatorException("Too many numbers");
        return nums.pop().value;
    }

    private static String modifyExpression(String expression) throws CalculatorException
    {
        expression = "(" + expression.replaceAll(" ", "") + ")";
        final String correctSymbols = "0123456789.+-*/()";
        String result = "(";
        for (int i = 1; i < expression.length(); ++i)
        {
            char cur = expression.charAt(i),
                 prev = expression.charAt(i-1);
            if (correctSymbols.indexOf(cur) == -1)
                throw new CalculatorException("Invalid symbol");
            if (cur == ')' && prev == '(')
                throw new CalculatorException("Empty brackets");
            if (cur == '-' && prev != ')' && !Character.isDigit(prev))
                cur = '!';
            result += cur;
        }
        return result;
    }
}
