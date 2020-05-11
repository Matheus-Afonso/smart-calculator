package com.mth.smartcalculator;

/*
 * Projeto do hyperskill - Smart Calculator: https://hyperskill.org/projects/42 
 * Insira uma expressão aritmética e obtenha o resultado.
 * Suporta:
 	* 5 tipos de operações aritméticas: +, -, *, /, ^
 	* Operações sem sinais simplificados: 8 +++ 7 -- 5 -> 20
 	* Expressões com parenteses: 25 * (5 + 3) - 8 -> 192
 	* Variaveis: a = 10; 5*a + 7 -> 57
 	* Comandos: /exit; /help; /var
 	* Números inteiros de valores maiores que Integer.MAX_VALUE: 500000000000 + 456789456123 -> 956789456123
*/

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class Main {
	
	private static final Map<String, String> variables = new HashMap<>();
	
	public static void main(String[] args) {
		System.out.println("Program Started. Type '/help' for more details");
		Scanner scanner = new Scanner(System.in);
		
        String ans = "";
        while(!ans.equals("Bye!")) {
        	String expression = scanner.nextLine();
        	try {
        		expression = simplify(expression);
        		
        		if(!expression.equals("")) {
        			ans = expressionTreatment(expression);
        			System.out.println(ans);
        		}
				
			} catch (InvalidExpressionException exc) {
				System.out.println(exc.getMessage());
			}
        }
        
        scanner.close();
	}
	
	private static String expressionTreatment(String expression) {
		// Tratamento de código
		if(expression.startsWith("/")) {
			return responseCode(expression);
		}
		
		// Tratamento de expressão comum
		
		return ExpressionSolver.solveExpression(expression).toString();
	}
	
	// Simplificar as expressões(tirar espaços, símbolos repetidos, etc)
	private static String simplify(String expression) {
		if(!expression.startsWith("/")) {
			
			if(expression.contains("=")) {
				return createVariable(expression);
			}
			
			return transformToValidExpression(expression);
		}
		
		return expression;
	}
	
	private static String reduceArithmeticSymbols(String expression) {
		boolean isReduced = false;
		
		while(!isReduced) {
			String reducedExpression = expression.
									replaceAll("(\\++)|(--)", "+").
									replaceAll("(\\+-)|(-\\+)", "-");
					
			// Checa se as mudanças já não fazem mais efeito
			if(reducedExpression.equals(expression)) {
				isReduced = true;
			} else {
				expression = reducedExpression;
			}
		}
		return expression;
	}
	
	private static String transformToValidExpression(String expression) {
		
		// Checa se possui espaços sem operações entre números
		if (expression.replaceAll("\\w\\s+\\w", "").length() != expression.length()) {
			throw new InvalidExpressionException("Invalid expression");
		}
		
		// Retira os espaços
		expression = expression.replaceAll("\\s", "");
		int normalSize = expression.length();
		
		// Checa se não possui caracteres invalidos
		if (expression.replaceAll("[^+\\-*/0-9A-z)(]", "").length() != normalSize) {
			throw new InvalidExpressionException("Invalid expression");
		}
		
		// Checa se não possui asteriscos ou barras seguidas
		if (expression.replaceAll("(\\*{2,})|(/{2,})", "").length() != normalSize) {
			throw new InvalidExpressionException("Invalid expression");
		}

		// Se possuir variaveis, substitui pelos valores corretos
		if (expression.replaceAll("[a-zA-Z]", "").length() != normalSize) {
			expression = changeVariablesToValues(expression);
		}
		
		// Checa se a expressão não termina com um símbolo
		if (!expression.matches(".*[\\d)]$") && !expression.isEmpty()) {
			throw new InvalidExpressionException("Invalid expression");
		}
		
		// Após tudo, simplifique as somas e subtracoes como ++++, ---, ...
		return reduceArithmeticSymbols(expression);
	}

	private static String createVariable(String expression) {
		expression = expression.replaceAll("\\s", "");
		
		// Checa se está atribuindo variáveis indiretamente e regula
		if(expression.matches("^[A-z]+=[A-z]+$")) {
			String varName = expression.replaceAll(".*=", "");
			String value = extractVariableValue(varName);
			expression = expression.replace(varName, value);
		} else if(!expression.matches("^([A-z]+)=([+-]?\\d+)$")) {
			// Checa qual parte está errada. Esquerda ou direita
			if(!expression.matches("^([A-z]+)=.*")) {
				throw new InvalidExpressionException("Invalid identifier");
			} else {
				throw new InvalidExpressionException("Invalid assignment");
			}
		}
		
		// Checa se esta atribuindo variáveis diretamente 
		String[] pair = expression.split("=");
		variables.put(pair[0], pair[1]);
		
		return "";
	}
	
	private static String extractVariableValue(String varName) {
		// Se a key não for achada, joga o Exception
		return Optional.
				ofNullable(variables.get(varName)).
				orElseThrow(() -> new InvalidExpressionException("Unknown variable"));
	}
	
	private static String changeVariablesToValues(String expression) {
		Pattern keyPattern = Pattern.compile("[A-z]+");
		Matcher keyMatcher = keyPattern.matcher(expression);
		
		while(keyMatcher.find()) {
			String varName = keyMatcher.group();
			expression = expression.replace(varName, extractVariableValue(varName));
		}

		return expression;
	}
	
	private static String responseCode(String code) {
		if(code.equals("/exit")) {
			return "Bye!";
		} else if(code.equals("/help")) {
			return "The program calculates the expression. Examples of valid expressions: \n" +
					"2 * (25+8)-1 \n" +
					"9 +++ 10 -- 8 \n" +
					"14  *   12 \n" +
					"a=4 \n" +
					"a*2+b*3+c*(2+3) \n" +
					"Examples of commands: \n" + 
					"/help: prints this message \n" +
					"/var: show all variables value \n" +
					"/exit: terminate program";
		} else if (code.equals("/var")) {
			variables.forEach((k,v) -> System.out.println(k + " = " + v));
			return "";
		} else {
			return "Unknown command";
		}
	}
}
