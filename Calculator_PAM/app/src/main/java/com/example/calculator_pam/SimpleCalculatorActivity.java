package com.example.calculator_pam;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.mariuszgromada.math.mxparser.Expression;

public class SimpleCalculatorActivity extends AppCompatActivity {

    protected TextView inputTextView, expressionTextView;
    protected String currentInput = "";
    protected String expression = "";

    protected boolean isClearPreviousInput = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simple_calculator);

        initTextViews();
        setButtonsOnClickListeners();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("currentInput", currentInput);
        outState.putString("expression", expression);
        outState.putBoolean("isClearPreviousInput", isClearPreviousInput);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        currentInput = savedInstanceState.getString("currentInput");
        expression = savedInstanceState.getString("expression");
        isClearPreviousInput = savedInstanceState.getBoolean("isClearPreviousInput");
        expressionTextView.setText(expression);
        inputTextView.setText(currentInput);
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void initTextViews() {
        inputTextView = findViewById(R.id.calc_input);
        expressionTextView = findViewById(R.id.calc_expression);
    }

    protected void setButtonsOnClickListeners() {
        // Operators
        findViewById(R.id.Btn0).setOnClickListener(v -> addOperand("0"));
        findViewById(R.id.Btn1).setOnClickListener(v -> addOperand("1"));
        findViewById(R.id.Btn2).setOnClickListener(v -> addOperand("2"));
        findViewById(R.id.Btn3).setOnClickListener(v -> addOperand("3"));
        findViewById(R.id.Btn4).setOnClickListener(v -> addOperand("4"));
        findViewById(R.id.Btn5).setOnClickListener(v -> addOperand("5"));
        findViewById(R.id.Btn6).setOnClickListener(v -> addOperand("6"));
        findViewById(R.id.Btn7).setOnClickListener(v -> addOperand("7"));
        findViewById(R.id.Btn8).setOnClickListener(v -> addOperand("8"));
        findViewById(R.id.Btn9).setOnClickListener(v -> addOperand("9"));
        findViewById(R.id.dotBtn).setOnClickListener(v -> addOperand("."));

        //Operands
        findViewById(R.id.addBtn).setOnClickListener(v -> addOperator("+"));
        findViewById(R.id.subBtn).setOnClickListener(v -> addOperator("-"));
        findViewById(R.id.divBtn).setOnClickListener(v -> addOperator("÷"));
        findViewById(R.id.mulBtn).setOnClickListener(v -> addOperator("×"));

        //Invert
        findViewById(R.id.invertBtn).setOnClickListener(v -> invertNumber());

        //Clear
        findViewById(R.id.clearBtn).setOnClickListener(v -> clear());
        findViewById(R.id.allClearBtn).setOnClickListener(v -> allClear());

        //Calculate
        findViewById(R.id.resultBtn).setOnClickListener(v -> calculate());
    }

    protected void addOperand(String operand) {
        isClearPreviousInput = false;

        // Handle zero
        if(currentInput.equals("0")) {
            if (operand.equals("0")) {
                // Prevent multiple zeros
                return;
            } else {
                currentInput = "";
            }
        }

        // Handle decimal point
        if(operand.equals(".")) {
            if(currentInput.isEmpty()) {
                currentInput+="0";
            } else if (currentInput.contains(".")) { // avoid double dot
                Toast.makeText(this, "The number already contains decimal point.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if(!"0123456789".contains(operand)) {
            Toast.makeText(this, "Invalid operand input", Toast.LENGTH_SHORT).show();
            return; // return if operand is invalid
        }

        // Add operand to currentInput string
        currentInput+=operand;
        inputTextView.setText(currentInput);
    }

    protected void addOperator(String operator) {
        isClearPreviousInput = false;
        String operators = "+-÷×";
        if(currentInput.endsWith(".")) {
            currentInput+="0";
        }

        //validate operator
        if(currentInput.isEmpty()) {
            Toast.makeText(this, "Number must be inputted prior to operation.", Toast.LENGTH_SHORT).show();
            return;
        } else if (expression.length() >= 2 && operators.contains(String.valueOf(expression.charAt(expression.length() - 1)))) {
            Toast.makeText(this, "Operator can't be followed by another operator.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Move current input to expression and add operator
        expression = expression + currentInput + " " + operator + " ";
        // Clear current input
        currentInput = "";

        expressionTextView.setText(expression);
        inputTextView.setText(currentInput);
    }

    private void clear() {
        if(!isClearPreviousInput) {
            isClearPreviousInput = true;
            currentInput = "";
            inputTextView.setText(currentInput);
        }
        else {
            allClear();
        }
    }

    private void allClear() {
        currentInput = "";
        expression = "";
        inputTextView.setText(currentInput);
        expressionTextView.setText(expression);
    }

    private void invertNumber() {
        isClearPreviousInput = false;
        if(currentInput.isEmpty()) {
            Toast.makeText(this, "Input number first.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double currentInputValue = Double.parseDouble(currentInput);
            if(currentInputValue == 0) {
                Toast.makeText(this, "Zero can't be negative.", Toast.LENGTH_SHORT).show();
                return;
            }
            currentInput = String.valueOf(-currentInputValue);
            inputTextView.setText(currentInput);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void calculate() {
        isClearPreviousInput = false;
        if(currentInput.endsWith(".")) {
            currentInput+= "0";
        }

        expression+= currentInput;
        expressionTextView.setText(expression);

        Expression exp = new Expression(expression);
        double result = (new Expression(expression)).calculate();

        if (Double.isNaN(result)) {
            Toast.makeText(this, "Invalid calculation.", Toast.LENGTH_SHORT).show();
            currentInput = "";
            expression = "";
            inputTextView.setText("");
        } else {
            currentInput = String.valueOf(result);
            inputTextView.setText(currentInput);
            expression = "";
        }
    }

}
