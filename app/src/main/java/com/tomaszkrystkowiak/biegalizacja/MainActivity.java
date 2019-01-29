package com.tomaszkrystkowiak.biegalizacja;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(new MapButtonClick());

    }

    private void startMapActivity(){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


    private class MapButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            startMapActivity();
        }
    }
}
