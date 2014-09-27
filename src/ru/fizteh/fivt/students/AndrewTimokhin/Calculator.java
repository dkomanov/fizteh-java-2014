import java.util.*;
//Author: Timokhin Andrew 295 Group
//грамматика построени€ стандартна€

class Errors extends Exception {
	String err_msg;
	Errors (String msg) {err_msg=msg;};
	public String toString () {
		return err_msg;
	}
}
 class Parser {  
	final int SINTERROR=0; //коды ошибок
	final int DIVIDEDBYZEROERROR=1;
	final int EMPTYERROR=2;
	final int UBSKOBKIERROR=3;
	//типы лексем на UML диаграмме
	
	final int UNKNOW=0;
	final int LITERAL=1;
	final int DIGITAL=2;
	
	//данные необходимые дл€ получени€ очередной лексеммы
	
	private String exp; //заданна€ на парсер искома€ строка
	private String token; //текущ€€ распознанна€ лексемма
	private int expldx; //позици€ текущей лексеммы в строке
	private int tokType; //тип текущей лексеммы
private void control (int error_code) throws Errors

{
String []msg= 	 {">>Syntax error : code 0",">>Divide by Zero : code 1", ">>>Empty : code 2", ">>UB() : code 3"};
throw new Errors(msg[error_code]);
}
private void getToken () throws Errors{ 
	tokType=UNKNOW;  //тип начальной лексеммы неизвестен 
	token=""; // занул€ем каждый раз т.к. мы используем рекурсивно-последовательный синтаксический анализатор 
	if (expldx==exp.length()) {   //если мы указываем на последний элемент строки => парсер уже все распарсил
		token = "\0";
		return;
	}
	if ( (check(exp.charAt(expldx)))) { //если у нас разграничители=> то мы провер€ем входит ли данный символ в множество разграничителей
		token+=exp.charAt(expldx); //запоминаем текущую лексемму 
		expldx++; //итерируем на 1, дл€ указани€ на следующую лексемму 
		tokType= LITERAL; //тип лексеммы- литеральна€
 
	} else if (Character.isDigit(exp.charAt(expldx))){
		while ( (!check(exp.charAt(expldx)))) { //аналогично 1 пункту, только здесь мы будем анализировать число 
			
			   //если очередна€ цифра достигла конца строки => парсер закончил свою работу
			
			 
			
			
		token+=	exp.charAt(expldx); 
		expldx++;
		if (expldx>=exp.length()) break;
		 
		} tokType= DIGITAL;  //тип лексеммы, разумеетс€ цифровой
		
		 
	}
	
	
	
}

double eval (String str) throws Errors{
	double result ;
	exp=str;  //запоминаем нашу строку, котора€ подлежит парсингу в переменной exp
	 expldx=0; //устанавливаем начальное значение просмотренной лексеммы в 0
	 getToken (); //получаем лексемму 
	 if (token=="\0")
		 control (2);
	 result= Step1(); //запускаем рекурсивный разбор
	 if (token!="\0")
		 control (0);
	 return result; //возращаем наш результат
	 
	
	
}
	
private double Step1 () throws Errors{
	double result; double localresult; 
	
	 
	
 
			 result= Step2( ); //передаем дальше 
	char ch;   
	while (  (ch=token.charAt(0))=='+' || ch=='-') {
		getToken ();    localresult=Step2();
		 switch (ch){
		 case '-' :result=result-localresult;
		 break;
		 
		 case '+': result=result+localresult;
		 break;
		 
		 
		 }
		 
	}
	return result;
}


private double Step2 () throws Errors {
	 
	 
	double result; double localresult; 
	result= StepUnary( ); //передаем дальше 
	char ch;
	while (  (ch=token.charAt(0))=='*' || ch=='/') {
		getToken (); localresult=StepUnary();
	 	 

	 
		
	 switch (ch){
 case '*': result=result*localresult;
 break;
 
 case '/' : if (localresult==0) control(1); result=result/localresult;
 break;
 }
		
	}
	return result;
}
private double StepUnary() throws Errors{
	 
	double result; 
	String operation;
	operation="";
	if ((tokType==LITERAL) && token.equals("+")|| token.equals("-")) {
	operation=token;getToken();};

 
	result=Skobki();
	if (operation.equals("-") ) result=-result;
	return result;
	
	}
	
private double Skobki () throws Errors{
	 
	double result;
	if (token.equals ("(")) 
	{
		getToken ();
		result= Step1( );
		if (!token.equals(")") )control (3);
		getToken ();
		
		
	} else { result= prost();
 
	}
	return result ;
	
	
	
	
}

private double prost()throws Errors {
	double result=0.0;
	switch (tokType) {
	case 2:
		try {
		 result= Double.parseDouble(token);
		} catch (NumberFormatException e) {
			control (0);
		}
		 
		 getToken();
		 
		 break;
	default:
		control (0);
		break;
	 }
 
	 return result; 
	
	
	
	
}
private boolean check (char c ) throws Errors{
if (" ()+-*/".indexOf(c)!=-1)	 //станадртный метод indexOf €вл€етс€ методом класса String, в данном случае определ€ет вхождение символа в строку, и возращает индекс этого вхождени€
	
	return true; else return false;
}
}




class Demo {
 
	public static void main (String [] args) {
		//System.out.println(">>Enter String>> : ->");
		Scanner sc= new Scanner(System.in);
	String a= sc.next();
	
		Parser myparser= new Parser();
		try {
	double checksumm=	myparser.eval(a);  
	System.out.println(checksumm); }
		catch(Errors err) {
			System.out.println(err);
		}
		 sc.close();
	}
	 
}