import java.util.Stack;

public class Minus extends Operator
{
    public byte priority()
    {
        return 0;
    }

    public void operate (Stack<Operand> nums) throws CalculatorException
    {
        try
        {
            nums.push(new Operand(nums.pop().value.subtract(nums.pop().value).negate()));
        }
        catch (Exception e)
        {
            throw new CalculatorException(e.getMessage());
        }
    }
}
