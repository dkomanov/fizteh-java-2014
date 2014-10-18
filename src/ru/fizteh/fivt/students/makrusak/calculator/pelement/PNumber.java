package pelement;

import java.math.BigDecimal;
import java.util.Stack;

class PNumber extends PElement {
  private BigDecimal value;

  public PNumber(String s) {
    value = new BigDecimal(s);
  }

  @Override
  public void apply(Stack<BigDecimal> st) {
    st.push(value);
  }
}
