import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.text.BadLocationException;

public class Calculator {
    private JTextArea calculation;
    private JTextArea answer;
    private JButton clear;
    private JButton delete;
    private JButton division;
    private JButton seven;
    private JButton eight;
    private JButton nine;
    private JButton mulitplication;
    private JButton four;
    private JButton five;
    private JButton six;
    private JButton subtraction;
    private JButton one;
    private JButton two;
    private JButton three;
    private JButton addition;
    private JButton zero;
    private JButton decimal;
    private JButton enter;
    private JPanel calcPanel;
    private static Calculator instance;
    private boolean canUseDecimal = true;
    private boolean canUseOperator = false;
    private int charCount = 0;

    private Calculator() {
        zero.addActionListener(e -> {
            calculation.append("0");
            canUseOperator = true;
            charCount++;
        });
        one.addActionListener(e -> {
            calculation.append("1");
            canUseOperator = true;
            charCount++;
        });
        two.addActionListener(e -> {
            calculation.append("2");
            canUseOperator = true;
            charCount++;
        });
        three.addActionListener(e -> {
            calculation.append("3");
            canUseOperator = true;
            charCount++;
        });
        four.addActionListener(e -> {
            calculation.append("4");
            canUseOperator = true;
            charCount++;
        });
        five.addActionListener(e -> {
            calculation.append("5");
            canUseOperator = true;
            charCount++;
        });
        six.addActionListener(e -> {
            calculation.append("6");
            canUseOperator = true;
            charCount++;
        });
        seven.addActionListener(e -> {
            calculation.append("7");
            canUseOperator = true;
            charCount++;
        });
        eight.addActionListener(e -> {
            calculation.append("8");
            canUseOperator = true;
            charCount++;
        });
        nine.addActionListener(e -> {
            calculation.append("9");
            canUseOperator = true;
            charCount++;
        });
        addition.addActionListener(e -> {
            if (canUseOperator) {
                calculation.append("+");
                canUseOperator = false;
                canUseDecimal = true;
                charCount++;
            }
        });
        subtraction.addActionListener(e -> {
            if (canUseOperator) {
                calculation.append("-");
                canUseOperator = false;
                canUseDecimal = true;
                charCount++;
            }
        });
        mulitplication.addActionListener(e -> {
            if (canUseOperator) {
                calculation.append("*");
                canUseOperator = false;
                canUseDecimal = true;
                charCount++;
            }
        });
        division.addActionListener(e -> {
            if (canUseOperator) {
                calculation.append("/");
                canUseOperator = false;
                canUseDecimal = true;
                charCount++;
            }
        });
        decimal.addActionListener(e -> {
            if (canUseDecimal) {
                calculation.append(".");
                canUseDecimal = false;
                canUseOperator = false;
                charCount++;
            }
        });
        delete.addActionListener(e -> {
            if(charCount > 0) {
                try {
                    deleteCharacter();
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        clear.addActionListener(e -> {
            calculation.setText("");
            answer.setText("");
            charCount = 0;
        });
        enter.addActionListener(e -> {
            if(charCount > 0) {
                double solution;
                try {
                    solution = calculateAnswer(calculation.getText());
                } catch (ScriptException ex) {
                    throw new RuntimeException(ex);
                }
                answer.setText(String.valueOf(solution));
            }
        });
    }

    public static Calculator getInstance() {
        if (instance == null) {
            synchronized (Calculator.class) {
                if (instance == null)
                    instance = new Calculator();
            }
        }
        return instance;
    }

    public JPanel getPanel() {
        return calcPanel;
    }

    private void deleteCharacter() throws BadLocationException {
        String lastChar = calculation.getText(charCount - 1, 1);
        calculation.setText(calculation.getText(0, charCount - 1).substring(0, charCount - 1));
        charCount--;
        fixParameters(lastChar);
    }

    private void fixParameters(String removed) throws BadLocationException {
        if (charCount == 0) {
            canUseDecimal = true;
            canUseOperator = false;
        }
        else {
            char removedChar = removed.charAt(0);
            char lastChar = calculation.getText(charCount - 1, 1).charAt(0);
            if (removedChar == '.') {
                canUseDecimal = true;
                if (lastChar != '+' && lastChar != '-' && lastChar != '*' && lastChar != '/')
                    canUseOperator = true;
            }
            else if (removedChar == '+' || removedChar == '-' || removedChar == '*' || removedChar == '/') {
                canUseOperator = true;
                int index = charCount - 1;
                while (index > 0) {
                    lastChar = calculation.getText(index, 1).charAt(0);
                    if(lastChar == '.') {
                        canUseDecimal = false;
                        return;
                    }
                    else if (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/')
                        break;
                    index--;
                }
                canUseDecimal = true;
            }
            else {
                if (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/')
                    canUseOperator = false;
            }
        }
    }

    private double calculateAnswer(String calculationStr) throws ScriptException {
        if (calculationStr.endsWith(".") || calculationStr.endsWith("+") || calculationStr.endsWith("-") ||
                calculationStr.endsWith("*") || calculationStr.endsWith("/")) {
            calculationStr = calculationStr.substring(0, calculationStr.length() - 1);
        }

        int index = 0;
        double total = 0;
        String num = "";
        char op = '0';

        while (index < calculationStr.length()) {
            if (calculationStr.charAt(index) == '+' || calculationStr.charAt(index) == '-' ||
                    calculationStr.charAt(index) == '*' || calculationStr.charAt(index) == '/') {
                op = calculationStr.charAt(index);
                total = Double.parseDouble(num);
                index++;
                break;
            }
            num += calculationStr.charAt(index);
            index++;
        }

        if (index == calculationStr.length())
            return Double.parseDouble(num);

        num = "";

        for (int i = index; i < calculationStr.length(); i++) {
            if (calculationStr.charAt(i) == '+' || calculationStr.charAt(i) == '-' ||
                    calculationStr.charAt(i) == '*' || calculationStr.charAt(i) == '/') {
                total = getTotal(total, num, op);
                op = calculationStr.charAt(i);
                num = "";
                continue;
            }
            num += calculationStr.charAt(i);
        }

        total = getTotal(total, num, op);

        return total;
    }

    private double getTotal(double total, String num, char op) {
        switch (op) {
            case '+':
                total += Double.parseDouble(num);
                break;
            case '-':
                total -= Double.parseDouble(num);
                break;
            case '*':
                total *= Double.parseDouble(num);
                break;
            case '/':
                total /= Double.parseDouble(num);
                break;
        }
        return total;
    }
}
