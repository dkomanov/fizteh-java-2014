package pelement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

class PDivide extends PElement {

  @Override
  public void apply(Stack<BigDecimal> st) {
    BigDecimal a = st.pop();
    st.push(st.pop().divide(a, 5, RoundingMode.HALF_UP));
  }

}
