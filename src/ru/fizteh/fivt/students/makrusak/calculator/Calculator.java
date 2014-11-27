import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

//To multiply use "x"

import pelement.*;

public class Calculator {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Pass string as argument.");
      return;
    }
    String input = args[0];
    BigDecimal answer = null;
    ArrayList<PElement> polandList = null;

    try {
      polandList = buildReversePolandNotation(input);
    } catch (Exception e) {
      System.out.println("Incorrect input.");
      System.exit(1);
    }
    try {
      answer = calculateReversePolandNotation(polandList);
    } catch (Exception e) {
      System.out.println("Calculation error.");
      System.exit(1);
    }
    System.out.println(answer);
  }

  private static ArrayList<PElement> buildReversePolandNotation(String input)
      throws Exception {
    ArrayList<String> splitted = splitAndCompress(input);
    ArrayList<String> temp = new ArrayList<String>();
    Stack<String> tempStack = new Stack<String>();

    boolean wasOper = true;
    for (String c : splitted) {
      if (c.equals("-") && wasOper) {
        c = "u" + c;
        tempStack.push(c);
      } else if (c.equals("-") || c.equals("+")) {
        if (!tempStack.empty()) {
          String s = tempStack.lastElement();
          while (!tempStack.empty()
              && (s.equals("+") || s.equals("-") || s.equals("x") || s.equals("/"))) {
            temp.add(s);
            tempStack.pop();
            if (!tempStack.empty()) {
              s = tempStack.lastElement();
            }
          }
        }
        tempStack.push(c);
        wasOper = true;
      } else if (c.equals("x") || c.equals("/")) {
        if (!tempStack.empty()) {
          String s = tempStack.lastElement();
          while (!tempStack.empty()
              && (s.equals("x") || s.equals("/"))) {
            temp.add(s);
            tempStack.pop();
            if (!tempStack.empty()) {
              s = tempStack.lastElement();
            }
          }
        }
        tempStack.push(c);
        wasOper = true;
      } else if (c.equals("(")) {
        tempStack.push(c);
        wasOper = true;
      } else if (c.equals(")")) {
        while (!tempStack.empty()
            && !tempStack.lastElement().equals("(")) {
          temp.add(tempStack.pop());
        }
        if (tempStack.empty()) {
          throw new Exception();
        }
        tempStack.pop();
        if (!tempStack.empty() && tempStack.lastElement().equals("u-")) {
          temp.add(tempStack.pop());
        }
        wasOper = false;
      } else {
        temp.add(c);
        wasOper = false;
      }
    }

    while (!tempStack.empty() && !tempStack.lastElement().equals("(")) {
      temp.add(tempStack.pop());
    }
    if (!tempStack.empty()) {
      throw new Exception();
    }
    ArrayList<PElement> polandList = new ArrayList<PElement>();
    for (String s : temp) {
      polandList.add(PElement.create(s));
    }
    return polandList;
  }

  private static ArrayList<String> splitAndCompress(String input)
      throws Exception {
    input = input.replace(" ", "");
    ArrayList<String> splitted = new ArrayList<String>();
    String cur = "";
    for (char c : input.toCharArray()) {
      if (c == '/' || c == 'x' || c == '+' || c == '-' || c == '('
          || c == ')') {
        if (!cur.equals("")) {
          splitted.add(cur);
          cur = "";
        }
        splitted.add(Character.toString(c));
      } else if (c == '.' || (c >= '0' && c <= '9')) {
        if (cur.equals("") && c == '.') {
          throw new Exception();
        }
        cur += c;
      } else {
        throw new Exception();
      }
    }
    if (!cur.equals("")) {
      splitted.add(cur);
    }
    return splitted;
  }

  private static BigDecimal calculateReversePolandNotation(
      ArrayList<PElement> polandList) throws Exception {

    Stack<BigDecimal> st = new Stack<BigDecimal>();
    for (PElement t : polandList) {
      t.apply(st);
    }
    if (st.size() != 1) {
      throw new Exception();
    } else {
      BigDecimal t = st.pop();
      return t;
    }
  }
}
