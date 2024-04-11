package com.example.calculator_pam;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SimpleCalculatorActivity extends AppCompatActivity {

    protected TextView inputTextView, outputTextView;
    protected String expression, currentInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setButtonsOnClickListeners() {
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

    private void addOperand(String operand) {
        //TODO
    }

    private void addOperator(String operator) {
        //TODO
    }

    private void invertNumber() {
        //TODO
    }

    private void calculate() {
        //TODO
    }

    private void clear() {
        //TODO
    }

    private void allClear() {
        //TODO
    }


}
