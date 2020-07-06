package smartcalculator;

/*
 * Smart Calculator
 * Insira uma expressão aritmética e obtenha o resultado.
*/

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class Main {
	
	private static final Map<String, String> variables = new HashMap<>();
	private static final String INVALID_EXPRESSION = "Expressao Invalida";
	private static final String INVALID_VAR_NAME = "Nome de variavel invalido";
	
	public static void main(String[] args) {
		System.out.println("Programa iniciado. Digite '/help' para mais detalhes");
		Scanner scanner = new Scanner(System.in);
		
        String ans = "";
        while(!ans.equals("Bye!")) {
        	System.out.print("> ");
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
			throw new InvalidExpressionException(INVALID_EXPRESSION);
		}
		
		// Retira os espaços
		expression = expression.replaceAll("\\s", "");
		
		// Substitui número junto a um parenteses/variável em uma multiplicação
		expression = expression.replaceAll("(\\d)([a-zA-Z(])", "$1*$2");
		expression = expression.replaceAll("([a-zA-Z])([(])", "$1*$2");
		
		int normalSize = expression.length();
		
		// Checa se não possui caracteres invalidos
		if (expression.replaceAll("[^+\\-*/0-9A-z)(]", "").length() != normalSize) {
			throw new InvalidExpressionException(INVALID_EXPRESSION);
		}
		
		// Checa se não possui asteriscos ou barras seguidas
		if (expression.replaceAll("(\\*{2,})|(/{2,})", "").length() != normalSize) {
			throw new InvalidExpressionException(INVALID_EXPRESSION);
		}
		
		// Checa se não possui variáveis de nome invalido. Ex: a5, n7n
		if (expression.replaceAll("[a-zA-Z]\\d", "").length() != normalSize) {
			throw new InvalidExpressionException(INVALID_VAR_NAME);
		}
		
		// Se possuir variaveis, substitui pelos valores corretos
		if (expression.replaceAll("[a-zA-Z]", "").length() != normalSize) {
			expression = changeVariablesToValues(expression);
		}
		
		// Checa se a expressão não termina com um símbolo
		if (!expression.matches(".*[\\d)]$") && !expression.isEmpty()) {
			throw new InvalidExpressionException(INVALID_EXPRESSION);
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
				throw new InvalidExpressionException(INVALID_VAR_NAME);
			} else {
				throw new InvalidExpressionException("Valor impossivel de atribuir");
			}
		}
		
		// Checa se esta atribuindo variáveis diretamente 
		String[] pair = expression.split("=[+]?");
		variables.put(pair[0], pair[1].startsWith("+") ? pair[1].replaceFirst("\\+","") : pair[1]);
		
		return "";
	}
	
	private static String extractVariableValue(String varName) {
		// Se a key não for achada, joga o Exception
		return Optional.
				ofNullable(variables.get(varName)).
				orElseThrow(() -> new InvalidExpressionException("Variavel desconhecida: '" + varName + "'"));
	}
	
	private static String changeVariablesToValues(String expression) {
		Pattern keyPattern = Pattern.compile("[a-zA-Z]+");
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
			return "Programa que calcula a expressao aritmetica. Exemplos de expressoes validas: \n" +
					"2 * (25+8)-1 \n" +
					"9 +++ 10 -- 8 \n" +
					"14  *   12 \n" +
					"a=4 \n" +
					"b = 3 \n" +
					"c = a \n" +
					"a*2+b*3+c*(2+3) \n" +
					"5a + b(3 + c) + c^2(30/2) \n" +
					"Comandos disponiveis: \n" + 
					"/help: Mostra essa mensagem\n" +
					"/var: Mostra o valor de todas as variaveis salvas \n" +
					"/exit: Encerra o programa \n";
		} else if (code.equals("/var")) {
			System.out.println("Valores de cada variavel:");
			variables.forEach((k,v) -> System.out.println(k + " = " + v));
			return "";
		} else {
			return "Comando: '" + code + "' nao reconhcido pelo sistema";
		}
	}
}
