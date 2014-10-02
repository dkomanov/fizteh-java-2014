package ru.fizteh.fivt.students.AndrewTimokhin.Calculator;

import java.util.*;

//@Author: Timokhin Andrew 295 Group

//class Parser have all logical structure by algorithm
class Calculator {  //class Calculator 
 public static void main(String [] args) {
 //System.out.println(">>Enter String>> : ->");
 //Scanner sc= new Scanner(System.in);
 try {
 String a = args[0].toString();
 //String a= sc.next();
 Parser myparser = new Parser();
 try {
 double checksumm = myparser.eval(a);  
 System.out.println(checksumm) ;
 } catch (Errors err) {
 System.out.println(err);
 } 
 } catch (ArrayIndexOutOfBoundsException e) {
 System.out.println("An error was detected : " + e);
 }
 } }
class Errors extends Exception {
 private static final long serialVersionUID = 666;
 String errmsg;
 Errors(String msg) { errmsg = msg; } ;
 public String toString() {
 return errmsg;
 }
}
 class Parser {  
 final int sinerror = 0; // error code
 final int dividedbyzero = 1;
 final int emptyerror = 2;
 final int ubskobkierror = 3;
 final int unknow = 0;  //type token
 final int literal = 1;
 final int digital = 2;
 private String exp;  //parsing source
 private String token;  
 private int expldx;  
 private int tokType;  
private void control(int errorcode) throws Errors  //generate exception by code error
{
String []msg = {">>Syntax error : code 0", ">>Divide by Zero : code 1", ">>>Empty : code 2", ">>UB() : code 3"};
throw new Errors(msg[errorcode]);
}
private void getToken() throws Errors {   // meth for next token 
 tokType = unknow;   
 token = "";  
 if (expldx == exp.length()) {    
 token = "\0";
 return;
 }
 if ((check(exp.charAt(expldx)))) {  
 token += exp.charAt(expldx);  
 expldx++;  
 tokType = literal;  
 } else if (Character.isDigit(exp.charAt(expldx))) {
 while ((!check(exp.charAt(expldx)))) {  
 token += exp.charAt(expldx); 
 expldx++;
 if (expldx >= exp.length()) { break; }
 } tokType = digital;   
 }
 }
double eval(String str) throws Errors { // pars start
 double result ;
 exp = str;   
 expldx = 0;  
 getToken(); 
 if (token.equals("\0")) {
 control(2);
 }
 result = step1();  
 if (!token.equals("\0")) {
 control(0);
 }
 return result;  
 }
 private double step1() throws Errors {  // meth for "+" and "-" operation
 double result; 
 double localresult; 
 result = step2();   // next rekursive action
 char ch;   
 while ((ch = token.charAt(0)) == '+' || ch == '-') {
 getToken(); 
 localresult = step2();
 switch (ch){
 case '-' :result = result - localresult;
 break;
 case '+': result = result + localresult;
 break;
 default : break;
 }
 }
 return result;
 }
 private double step2() throws Errors {  // meth for "/" and "*"
 double result; 
 double localresult; 
 result = stepunary();  
 char ch;
 while ((ch = token.charAt(0)) == '*' || ch == '/') {
 getToken(); 
 localresult = stepunary();
 switch(ch) {
 case '*' : result = result * localresult;
 break;
 case '/' : if (localresult == 0) { control(1); }
 result = result / localresult;
 break;
 default : break;
 }
}
 return result;
}
private double stepunary() throws Errors { // check, was "-" unary or binary
 double result; 
 String operation;
 operation = "";
 if ((tokType == literal) && token.equals("+") || token.equals("-")) {
 operation = token;
 getToken() ;
 }
 result = skobki();
 if (operation.equals("-")) { result = -result; }
 return result;
 }
private double skobki() throws Errors {
 double result;
 if (token.equals("(")) {
 getToken();
 result = step1();
 if (!token.equals(")")) {
 control(3);
 }
 getToken();
 } else { result = prost();
 }
 return result ;
}
 private double prost() throws Errors { // get digital 
 double result = 0.0;
 switch (tokType) {
 case 2:
 try {
 result = Double.parseDouble(token);
 } catch (NumberFormatException e) {
 control(0);
 }
 getToken();
 break;
 default:
 control(0);
 break;
 }
 return result; 
 }
 private boolean check(char c) throws Errors {
return (" ()+-*/".indexOf(c) != -1);
 }  
 }
