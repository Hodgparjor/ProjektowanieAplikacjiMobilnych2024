package com.example.calculator_pam;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class AdvancedCalculatorActivity extends SimpleCalculatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advanced_calculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initTextViews();
        setButtonsOnClickListeners();
        setAdvancedButtonsOnClickListeners();
    }

    private void setAdvancedButtonsOnClickListeners() {
        findViewById(R.id.sinBtn).setOnClickListener(v -> addOperator("sin"));
        findViewById(R.id.cosBtn).setOnClickListener(v -> addOperator("cos"));
        findViewById(R.id.tanBtn).setOnClickListener(v -> addOperator("tan"));
        findViewById(R.id.sqrtBtn).setOnClickListener(v -> addOperator("sqrt"));
        findViewById(R.id.logBtn).setOnClickListener(v -> addOperator("lg"));
        findViewById(R.id.lnBtn).setOnClickListener(v -> addOperator("ln"));
        findViewById(R.id.pow2Btn).setOnClickListener(v -> addOperator("^2"));
        findViewById(R.id.powYBtn).setOnClickListener(v -> addOperator("^"));
        findViewById(R.id.percentBtn).setOnClickListener(v -> percentage());
    }


    @Override
    protected void addOperator(String operator) {
        isClearPreviousInput = false;
        String operators = "+-÷×^sincostanlnsqrtlg";
        String basicOperators = "+-÷×";
        String unaryOperators = "sincostanlnsqrtlg";

        if(expression.isEmpty() && currentInput.isEmpty()) {
            Toast.makeText(this, "Expression must start with a number.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!expression.isEmpty()) {
            String[] tokens = expression.split(" ");
            String lastExpressionElement = tokens[tokens.length - 1].split("[(]")[0];

            if(!unaryOperators.contains(lastExpressionElement) && !(tokens[tokens.length - 1].contains("^") && !tokens[tokens.length - 1].endsWith("^")) && currentInput.isEmpty()) {
                Toast.makeText(this, "Number must be inputted prior to operator.", Toast.LENGTH_SHORT).show();
                return;
            }

            if((unaryOperators.contains(lastExpressionElement) ||
                    (tokens[tokens.length - 1].contains("^") && !tokens[tokens.length - 1].endsWith("^")))
                    && (unaryOperators.contains(operator) || operator.contains("^")) && currentInput.isEmpty()) {
                Toast.makeText(this, "Number must be inputted prior to unary operator.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(currentInput.endsWith(".")) {
            currentInput+="0";
        }

        if(basicOperators.contains(operator)) {
            expression = expression + currentInput + " " + operator + " ";
        }
        else if(unaryOperators.contains(operator)) {
            expression = expression + operator + "(" + currentInput + ") ";
        }
        else if(operator.equals("^")) {
            if(currentInput.charAt(0) == '-') {
                expression = expression + "(" + currentInput + ")" + operator;
            }
            else {
                expression = expression + currentInput + operator;
            }
        }
        else if(operator.equals("^2")) {
            if(currentInput.charAt(0) == '-') {
                expression = expression + "(" + currentInput + ")" + operator + " ";
            }
            else {
                expression = expression + currentInput + operator + " ";
            }
        }
        currentInput = "";
        inputTextView.setText(currentInput);
        expressionTextView.setText(expression);
    }

    @Override
    protected void addOperand(String operand) {
        String unaryOperators = "sincostanlnsqrtlg";
        if(!expression.isEmpty()) {
            String[] tokens = expression.split(" ");
            String lastExpressionElement = tokens[tokens.length - 1].split("[(]")[0];

            if(unaryOperators.contains(lastExpressionElement) && lastExpressionElement.length() > 1 || (tokens[tokens.length - 1].contains("^") && !tokens[tokens.length - 1].endsWith("^"))) {
                expression += " × ";
                expressionTextView.setText(expression);
            }
        }
        super.addOperand(operand);
    }

    private void percentage() {
        if(currentInput.isEmpty()) {
            Toast.makeText(this, "Number must be inputted first.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            currentInput = String.valueOf(Double.parseDouble(currentInput) / 100);
            inputTextView.setText(currentInput);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid percentage input.", Toast.LENGTH_SHORT).show();
        }
    }
}
