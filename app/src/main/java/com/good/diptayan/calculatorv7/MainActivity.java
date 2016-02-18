package com.good.diptayan.calculatorv7;

import java.text.DecimalFormat;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final String ADD = "\u002B";
    public static final String SUB = "\u2212";
    public static final String DIV = "\u00F7";
    public static final String MUL = "\u2715";
    public static final String PER= "\u2052";
    public String value = "";
    private boolean lastNumeric,lastDot;
    public LinkedList<String> operators = new LinkedList<String>();
//    TextView tvAns = (TextView) findViewById(R.id.txtScreen);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu); // There are no settings for this app.
        return true;
    }

    // Event handlers must be public, void, and have a view object as their only parameter!
    public void registerKey(View view)
    {
        switch(view.getId())
        {
            case R.id.btnZero:
                safelyPlaceOperand("0");
                break;
            case R.id.btnOne:
                safelyPlaceOperand("1");
                break;
            case R.id.btnTwo:
                safelyPlaceOperand("2");
                break;
            case R.id.btnThree:
                safelyPlaceOperand("3");
                break;
            case R.id.btnFour:
                safelyPlaceOperand("4");
                break;
            case R.id.btnFive:
                safelyPlaceOperand("5");
                break;
            case R.id.btnSix:
                safelyPlaceOperand("6");
                break;
            case R.id.btnSeven:
                safelyPlaceOperand("7");
                break;
            case R.id.btnEight:
                safelyPlaceOperand("8");
                break;
            case R.id.btnNine:
                safelyPlaceOperand("9");
                break;
            case R.id.btnDot:
                safelyPlaceDot(".");
                break;
            case R.id.btnAdd:
                safelyPlaceOperator(ADD);
                break;
            case R.id.btnSubtract:
                safelyPlaceOperator(SUB);
                break;
            case R.id.btnDivide:
                safelyPlaceOperator(DIV);
                break;
            case R.id.btnMultiply:
                safelyPlaceOperator(MUL);
                break;
            case R.id.btnPercent:
                safelyPlaceOperator(PER);
                break;
            case R.id.btnClear:
                resetCalculator();
                break;

        }
        display();
    }

    private void display()
    {
        TextView tvAns = (TextView) findViewById(R.id.txtScreen);
        tvAns.setText(value);
    }

    private void display(String s)
    {
        TextView tvAns = (TextView) findViewById(R.id.txtScreen);
        tvAns.setText(s);
    }

    private void safelyPlaceOperand(String op)
    {
        int operator_idx = findLastOperator();
        // Avoid double zeroes in cases where it's illegal (e.g. 00 -> 0, 01 -> 1, 1+01 -> 1+1).
        if (operator_idx != value.length()-1 && value.charAt(operator_idx+1) == '0')
            deleteTrailingZero();
        value += op;
        lastNumeric=true;
    }

    private void safelyPlaceOperator(String op)
    {
        lastNumeric=false;
        lastDot=false;
        if (endsWithOperator())  // Avoid double operators by replacing operator.
        {
            deleteFromLeft();
            value += op;
            operators.add(op);
        }
        else if (endsWithNumber())  // Safe to place operator right away.
        {
            value += op;
            operators.add(op);
        }
        // else: Operator by itself is an illegal operation, do not place operator.
    }
    private void safelyPlaceDot(String op)
    {

        TextView tvAns = (TextView) findViewById(R.id.txtScreen);
           /* int operator_idx = findLastOperator();

            // Avoid double zeroes in cases where it's illegal (e.g. 00 -> 0, 01 -> 1, 1+01 -> 1+1).
            if (operator_idx != value.length() - 1 && value.charAt(operator_idx + 1) == '0')
                deleteTrailingZero();
            if (operator_idx != value.length() - 1 && value.charAt(operator_idx + 1) == '.')
                deleteTrailingDot();
            value += op;*/

        if(!lastNumeric && !lastDot){
            value+=op;

            lastNumeric=true;
            lastDot=true;
        }
        if (lastNumeric && !lastDot) {
            value+=op;

            lastNumeric = false;
            lastDot = true;
        }




    }
    private void deleteTrailingDot()
    {
        if (value.endsWith(".")) deleteFromLeft();
    }
    private void deleteTrailingZero()
    {
        if (value.endsWith("0")) deleteFromLeft();
    }

    private void deleteFromLeft()
    {
        if (value.length() > 0)
        {
            if (endsWithOperator()) operators.removeLast();
            value = value.substring(0, value.length()-1);
        }
    }

    private boolean endsWithNumber()
    {
        // If we had a decimal point button, we'd have to grab the digit before it.
        return value.length() > 0 && Character.isDigit(value.charAt(value.length()-1));
    }

    private boolean endsWithOperator()
    {
        if (value.endsWith(ADD) || value.endsWith(SUB) || value.endsWith(MUL) || value.endsWith(DIV)) return true;
        else return false;
    }

    private int findLastOperator()
    {
        int add_idx = value.lastIndexOf(ADD);
        int sub_idx = value.lastIndexOf(SUB);
        int mul_idx = value.lastIndexOf(MUL);
        int div_idx = value.lastIndexOf(DIV);
        int per_idx=  value.lastIndexOf(PER);
        return Math.max(add_idx, Math.max(sub_idx, Math.max(mul_idx, Math.max(div_idx,per_idx))));
    }

    public void calculate(View view)  // Note: only called by the '=' button.
    {
        if (operators.isEmpty()) return;  // There is no operation to perform yet.
        if (endsWithOperator())
        {
            display("Math Error");
            resetCalculator();
            return;
        }

        // StringTokenizer is deprecated. Using String.split instead.
        String[] operands = value.split("\\u002B|\\u2212|\\u00F7|\\u2715|\\u2052");

        int i = 0;
        double ans = Double.parseDouble(operands[i]); // TODO: catch NumberFormatException?
        for (String operator : operators)
            ans = applyOperation(operator, ans, Double.parseDouble(operands[++i]));

        DecimalFormat df = new DecimalFormat("0.##########");
        display(df.format(ans));
        resetCalculator();
        // TODO: overwrite value with ans. reset value on next keypress if it's an operand (leave it if it's an operator)
    }

    private double applyOperation(String operator, double operand1, double operand2)
    {
        // Not using the string with switch syntax because Android doesn't support JDK 7 (JRE 1.7) yet.
        if (operator.equals(ADD)) return operand1 + operand2;
        if (operator.equals(SUB)) return operand1 - operand2;
        if (operator.equals(MUL)) return operand1 * operand2;
        if (operator.equals(PER)) return ((operand1/100)*operand2);
        if (operator.equals(DIV)) return operand1 / operand2;  // Don't need to check for div by 0, Java already does.
        lastDot=false;
        lastNumeric=false;
        return 0.0;
    }

    private void resetCalculator()
    {
        value = "";
        operators.clear();
        lastNumeric=false;
        lastDot=false;
    }
}