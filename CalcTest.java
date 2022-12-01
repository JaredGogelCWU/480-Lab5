/*
/Author: Jared Gogel
/Lab5
/“I  pledge  that  I  have  neither  given  nor  received  help  from  anyone  other  than  the
/instructor/TA for all program components included here!”
*/

import javax.script.ScriptException;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class CalcTest
{
    private char opChar;
    private String funSt;
    private double num1;
    private String exp;
    private String[] expressions;
    private double coCor;

    //Constructor
    public CalcTest(){
        num1 = 0.0;
        funSt = " ";
        opChar = ' ';
        exp = " ";
        expressions = null;
        coCor = 0.0;
    }
    //Method that chooses a random operator to add to the expression
    private String getRandOP()
    {
        Random rand = new Random();
        int op = rand.nextInt(4);

        switch (op)
        {
            case 0 -> opChar = '+';
            case 1 -> opChar = '-';
            case 2 -> opChar = '*';
            case 3 -> opChar = '/';
        }
        return Character.toString(opChar);
    }
    //Method that chooses a random function to add to the expression
    private String getRandFun()
    {
        Random rand = new Random();
        int fun = rand.nextInt(5);

        switch (fun) {
            case 0 -> funSt = "sin";
            case 1 -> funSt = "cos";
            case 2 -> funSt = "tan";
            case 3 -> funSt = "cot";
            case 4 -> funSt = "log";
        }
        return funSt;
    }
    //Method that chooses a whole number or double to add to the expression
    private String getNum()
    {
        Random rand = new Random();
        int num = rand.nextInt(2);

        double min = 0.0;
        double max = 100.0;

        switch (num)
        {
            case 0 -> num1 = rand.nextInt(1000);
            case 1 -> {
                double x = (Math.random() * ((max - min) + 1)) - min;
                num1 = Math.round(x * 10.0) / 10.0;
            }
        }
        return Double.toString(num1);
    }

    //Method that randomly chooses an expression and its contents to write to the file.
    private String createExp()
    {
        Random rand = new Random();
        int randExp = rand.nextInt(6);

        switch (randExp)
        {
            case 0 -> exp = getNum() + getRandOP() + getNum() + " ";
            case 1 -> exp = getRandFun()+ "(" + getNum()+ ")" + " ";
            case 2 -> exp = getNum() + getRandOP() + getRandFun() + "(" + getNum() + ")" + " " ;
            case 3 -> exp = "(" + getNum() + getRandOP() + getNum() + ")" + getRandOP() + "(" + getNum() + getRandOP() + getNum() + ")" + " ";
            case 4 -> exp = "(" + getRandFun() + "(" + getNum() + ")" + ")" + getRandOP() + "(" + getRandFun() + "(" + getNum() + ")" + ")" + " ";
            case 5 -> exp = getNum() + "^" + getNum() + " ";
           // case 6 -> exp =
        }

        return exp;
    }

    //Method to parse the expressions into an array
    private void readFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);

        while(scanner.hasNext()){
            expressions = scanner.nextLine().split(" ");
        }
    }

    //Evaluation Method used on Calculator
    public static double evalu(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                    //Added functionality for curly brackets in the calculator
                } else if (eat('{')) {
                    x = parseExpression();
                    if (!eat('}')) throw new RuntimeException("Missing '}'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(x);
                    else if (func.equals("cos")) x = Math.cos(x);
                    else if (func.equals("tan")) x = Math.tan(x);
                        //Added in a cot function, ln function, and log10 function
                    else if (func.equals("cot")) x = 1.0 / Math.tan(x);
                    //else if (func.equals("ln")) x = Math.log(x);
                    else if (func.equals("log")) x = Math.log(x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
    private void compare(double result1, double result2){
        if (result1 == result2){
            coCor++;
        }
    }


    public static void main(String[] args) throws IOException, ScriptException {
       CalcTest test =  new CalcTest();
       File file = new File("expressions.txt");
       try {
           //File file = new File("expressions.txt");
           if (file.createNewFile()) {
               System.out.println("File created: " + file.getName());
           } else {
               System.out.println("File already exists");
           }
       } catch (IOException e) {
           System.out.println("An error occurred.");
           e.printStackTrace();
           }

       Scanner scanner = new Scanner(System.in);
       System.out.println("How many expressions do you want generated?");
       int numExp = scanner.nextInt();

       try {
           FileWriter writer = new FileWriter("expressions.txt");
           for (int i = 0; i < numExp; i++) {
               writer.write(test.createExp());
           }
           writer.close();
           System.out.println("Success!");
       } catch (IOException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();
       }

       test.readFile(file);
       MathJSDemo math = new MathJSDemo();

       for (int i = 0; i < test.expressions.length; i++) {
         System.out.println("Expr" + i + ": " + test.expressions[i]);
         double x = evalu(test.expressions[i]);
         double result1 = Math.round(x * 10.0) / 10.0;
         System.out.println( "My Eval: " + result1);
         double z = Double.parseDouble(math.eval(test.expressions[i]));
         double result2 = Math.round(z * 10.0) / 10.0;
         System.out.println("Oracle Eval: " + result2);
         System.out.println(" ");
         test.compare(result1, result2);
       }
       double stat = ((test.coCor/numExp) * 100);
       System.out.println("In total out of " + numExp + " correctly generated expressions, rounded to the nearest tenth, in " + stat
                            + "% of the cases the internal eval agreed with the Oracle");
    }
}

