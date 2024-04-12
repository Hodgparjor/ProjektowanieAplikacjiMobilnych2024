package com.example.calculator_pam;

import android.os.Bundle;


public class AdvancedCalculatorActivity extends SimpleCalculatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_calculator);
        initTextViews();
        setButtonsOnClickListeners();
    }

    @Override
    protected void calculate() {
//        String ValueString = Value.toString();
//        ScriptEngineManager factory = new ScriptEngineManager();
//        ScriptEngine engine = factory.getEngineByName("JavaScript");
//        Double source = null;
//        formule = formule.replace("sin", "Math.sin").
//                replace("cos", "Math.cos").
//                replace("tan", "Math.tan").
//                replace("sqrt", "Math.sqrt").
//                replace("sqr", "Math.pow").
//                replace("log", "Math.log").
//                replace("x", ValueString);
//        try {
//            source = (Double)engine.eval(formule);
//        } catch(Exception exc) {
//            JOptionPane.showMessageDialog(null, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
//        }
        super.calculate();


    }
}
