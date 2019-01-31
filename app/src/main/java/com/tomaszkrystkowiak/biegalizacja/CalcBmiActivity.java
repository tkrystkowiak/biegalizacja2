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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calc);
        height_placeholder = (EditText) findViewById(R.id.height_placeholder);
        weight_placeholder = (EditText) findViewById(R.id.weight_placeholder);
        bmi_result = (TextView) findViewById(R.id.bmi_result);
    }

    public void calculateBmi(View v) {
        String firstV = height_placeholder.getText().toString();
        String secondV = weight_placeholder.getText().toString();

        if (firstV != null && secondV != null) {
            float heightValue = Float.parseFloat(firstV);
            float weightValue = Float.parseFloat(secondV);

            float bmi = weightValue / ((heightValue)/100 * heightValue/100);
            
            bmi_result.setText(new DecimalFormat("##.##").format(bmi));
        }
    }

}
