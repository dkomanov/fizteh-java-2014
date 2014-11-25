package pelement;

import java.math.BigDecimal;
import java.util.Stack;

class PMultiply extends PElement {

  @Override
  public void apply(Stack<BigDecimal> st) {
    BigDecimal a = st.pop();
    st.push(a.multiply(st.pop()));
  }

}
