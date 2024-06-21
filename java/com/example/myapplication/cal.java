package com.example.myapplication;

import java.util.Stack;

public class cal {

    public  Double calculation(String s) {
        String expression = s; // Example mathematical expression
        double result=0;
        try {
             result = evaluateExpression(expression);
            System.out.println("Result: " + result);
            return result;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());

        }
    return result;
    }

    private static double evaluateExpression(String expression) {
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == ' ')
                continue;
            else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                    double b = operands.pop();
                    double a = operands.pop();
                    operands.push(applyOperator(a, b, operators.pop()));
                }
                operators.push(ch);
            } else if (Character.isDigit(ch)) {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i));
                    i++;
                }
                i--;
                operands.push(Double.parseDouble(num.toString()));
            } else {
                throw new IllegalArgumentException("Invalid character in expression: " + ch);
            }
        }

        while (!operators.isEmpty()) {
            double b = operands.pop();
            double a = operands.pop();
            operands.push(applyOperator(a, b, operators.pop()));
        }

        return operands.pop();
    }

    private static int precedence(char operator) {
        if (operator == '+' || operator == '-')
            return 1;
        else if (operator == '*' || operator == '/')
            return 2;
        else
            return -1;
    }

    private static double applyOperator(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new ArithmeticException("Division by zero");
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
