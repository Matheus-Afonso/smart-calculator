# Smart Calculator
Programa que recebe e soluciona expressões aritméticas no console. Usa apenas números inteiros.

Suporta:
- Operações: ```+```, ```-```, ```*```, ```/``` e ```^```.
- Expressões com parênteses. 
- Criar e atualizar valores de variáveis.
- Números inteiros maiores que ```Integer.MAX_VALUE```.
- Comandos de auxílio: ```/help```, ```/var``` e ```/exit```.

Feito de acordo com o projeto [Smart Calculator](https://hyperskill.org/projects/42) do Hyperskill.

### Execução

O arquivo compilado .jar se encontra na pasta target.
```
cd target
java -jar calculator-1.0.jar
```
Também é possível clonar o repositório e executar em sua IDE de preferência como Eclipse, por exemplo.

### Comandos de auxílio

Comando | Descrição
------- | ---------
```/help``` | Retorna a descrição do programa e exemplos de expressões
```/var``` | Retorna a variáveis salvas na sessão atual
```/exit``` | Encerra o programa

### Exemplos

1. Expressão aritmética simplificada e não simplificada (Sabendo que ```++ = +``` e ```-- = -```)

![ex1](/images/ex1.PNG)

2. Expressão com parenteses:

![ex2](/images/ex2.PNG)

3. Salvar variáveis e usá-las. **As variáveis devem ser compostas apenas de letras**.

![ex3](/images/ex3.PNG)

4. Números inteiros muito grandes:

![ex4](/images/ex4.PNG)

5. Comandos:

![ex5](/images/ex5.PNG)
