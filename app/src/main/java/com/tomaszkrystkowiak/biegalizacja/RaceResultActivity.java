package com.tomaszkrystkowiak.biegalizacja;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RaceResultActivity extends AppCompatActivity {

    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_result);
        resultView.findViewById(R.id.resultView);
    }

    private void showResult(){
        long result = getIntent().getLongExtra("result",0l);
        if(result<0){
            resultView.setText("You lost by" +Math.abs(result)+"s");
        }
        else if(result>0){
            resultView.setText("Congratulations! You won by" +Math.abs(result)+"s");
        }
        else{
            resultView.setText("Draw");
        }
    }
}
