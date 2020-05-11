package com.mth.smartcalculator;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;


public final class ExpressionSolver {
	private static final Map<String, Integer> PRECEDENCE_ORDER = Map.of("+", 1, 
															"-", 1, 
															"*", 2,
															"/", 2, 
															"^", 3,
															"(", 4);
	private static Deque<String> operandStack;
	
	/**
	 * Precisa receber a expressao no formato String e sem espaços
	 * Ex: expression = 5*(4+10)-1
	*/
	
	private ExpressionSolver() {}
	
	public static BigInteger solveExpression(String infixExpression) {
		String postFixExp = convertToPostfix(infixExpression);
		return solvePostfix(postFixExp);
	}
	
	private static String convertToPostfix(String expression) {
		 operandStack = new ArrayDeque<>();

		// Retira os espaços em branco para não atrapalhar (Considere que os operandos já foram verificados)
		expression = expression.replaceAll("\\s" ,"");
		if(expression.startsWith("-")) {
			expression = expression.replaceFirst("-", "0-");
		} else if (expression.startsWith("+")) {
			expression = expression.replaceFirst("\\+", "");
		}
		
		String[] symbolArr = expression.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)|(?<=\\D)(?=\\D)");
		
		// Nova string onde irá ficar a nova expressão postfix
		StringBuilder postfixExp = new StringBuilder();
		for(String symbol: symbolArr) {
			if(isBigInteger(symbol)) {
				postfixExp.append(symbol).append(" ");
			} else {
				postfixExp.append(addOperandToStackAndReturnPostFix(symbol));
			}
		}

		// Checa se não sobrou nenhum parenteses no stack
		if(operandStack.contains("(") || operandStack.contains(")")) {
			throw new InvalidExpressionException("Invalid expression");
		}
		
		while(!operandStack.isEmpty()) {
			postfixExp.append(operandStack.pollLast()).append(" ");
		}
		return postfixExp.toString();
	}
	
	private static String addOperandToStackAndReturnPostFix(String operand) {
		StringBuilder postFixExp = new StringBuilder();
		String headSymbol = operandStack.peekLast();

		// Checa a prioridade de cada operando
		if ((operandStack.isEmpty() || headSymbol.equals("(")) && !operand.equals(")")) {
			operandStack.offerLast(operand);
		} else if (operand.equals(")")) {
			// Joga todos os operandos do stack para o postfix até achar o "("
			if(headSymbol == null) {
				throw new InvalidExpressionException("Invalid expression");
			}
			
			while(!headSymbol.equals("(")) {
				postFixExp.append(operandStack.pollLast()).append(" ");
				headSymbol = Optional
							.ofNullable(operandStack.peekLast())
							.orElseThrow(() -> new InvalidExpressionException("Invalid expression"));
			}

			// Quando achar o parenteses, joga ele fora
			operandStack.pollLast();

		} else {
			while (headSymbol != null
					&& !headSymbol.equals("(") 
					&& PRECEDENCE_ORDER.get(operand) <= PRECEDENCE_ORDER.get(headSymbol)) {
				// Retira todos os operandos de maior ou igual importância e manda para o posfix
				// Só depois de achar um operando menor ou um '(' que adiciona o symbol no stack
				postFixExp.append(operandStack.pollLast()).append(" ");
				headSymbol = operandStack.peekLast();
			}
			operandStack.offerLast(operand);
		}
		
		return postFixExp.toString();
	}
	
	private static BigInteger solvePostfix(String postFixExp) {
		// Para zerar o stack
		Deque<BigInteger> numberStack = new ArrayDeque<>();
		String[] postFixElemens = postFixExp.split("\\s");
		
		for(String elem: postFixElemens) {
			if(isBigInteger(elem)) {
				numberStack.offerLast(new BigInteger(elem));
			} else {
				BigInteger num2 = numberStack.pollLast();
				BigInteger num1 = numberStack.pollLast();
				numberStack.offerLast(symbolOperation(num1, num2, elem));
			}
		}
		
		return numberStack.peekLast();
	}
	
	private static boolean isBigInteger(String symbol) {
		try {
			new BigInteger(symbol);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private static BigInteger symbolOperation(BigInteger num1, BigInteger num2, String symbol) {
		switch (symbol) {
			case "+":
				return num1.add(num2);
			case "-":
				return num1.subtract(num2);
			case "*":
				return num1.multiply(num2);
			case "/":
				return num1.divide(num2);
			case "^":
				return num1.pow(num2.intValue());
			default:
				throw new InvalidExpressionException("Error: Aritmethic symbol '" + symbol + "' is not valid");
		}
	}

}
