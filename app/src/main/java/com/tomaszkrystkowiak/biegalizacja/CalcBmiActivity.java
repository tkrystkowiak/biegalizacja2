package com.tomaszkrystkowiak.biegalizacja;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CalcBmiActivity extends Activity {

    private EditText height_placeholder;
    private EditText weight_placeholder;
    private TextView bmi_result;
    private TextView bmi_advise;
    private TextView bmi_diff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calc);
        height_placeholder = (EditText) findViewById(R.id.height_placeholder);
        weight_placeholder = (EditText) findViewById(R.id.weight_placeholder);
        bmi_result = (TextView) findViewById(R.id.bmi_result);
        bmi_advise = (TextView) findViewById(R.id.bmi_advise);
        bmi_diff = (TextView) findViewById(R.id.bmi_diff);
    }

    public void calculateBmi(View v) {
        String firstV = height_placeholder.getText().toString();
        String secondV = weight_placeholder.getText().toString();

        if (firstV != null && secondV != null) {
            float heightValue = Float.parseFloat(firstV);
            float weightValue = Float.parseFloat(secondV);

            float bmi = weightValue / ((heightValue)/100 * heightValue/100);

            bmi_result.setText(new DecimalFormat("##.##").format(bmi));

            if (bmi < 18.50) {
                bmi_advise.setText("Woah! It's a serious underweight. Eat more!");
                float diff = (19 *((heightValue)/100)*((heightValue)/100)) - weightValue;
                bmi_diff.setText(String.valueOf("You need to gain " + new DecimalFormat("##.#").format(diff) + " kg!"));
            } else if (bmi < 25.00) {
                bmi_advise.setText("Healthy weight! You are in a good shape - keep going!");
                bmi_diff.setText("You are on good path!");
            } else if (bmi < 29.99) {
                bmi_advise.setText("You are overweight! Are you ready for next training?");
                float diff = weightValue - (25 *((heightValue)/100)*((heightValue)/100));
                bmi_diff.setText(String.valueOf("You need to lose " + new DecimalFormat("##.#").format(diff) + " kg!"));
            } else {
                bmi_advise.setText("It's serious Obese. Please contact with your doctor!");
                float diff = weightValue - (25 *((heightValue)/100)*((heightValue)/100));
                bmi_diff.setText(String.valueOf("You need to lose " + new DecimalFormat("##.#").format(diff) + " kg!"));
            }

        }

    }


}
