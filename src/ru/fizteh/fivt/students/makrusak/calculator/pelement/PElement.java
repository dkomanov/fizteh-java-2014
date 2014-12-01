package pelement;

import java.math.BigDecimal;
import java.util.Stack;

public abstract class PElement {
  public static PElement create(String s) {
    if (s.equals("+")) {
      return new PPlus();
    } else if (s.equals("/")) {
      return new PDivide();
    } else if (s.equals("x")) {
      return new PMultiply();
    } else if (s.equals("-")) {
      return new PMinus();
    } else if (s.equals("u-")) {
      return new PUnaryMinus();
    } else {
      return new PNumber(s);
    }
  }

  public abstract void apply(Stack<BigDecimal> st);
}
