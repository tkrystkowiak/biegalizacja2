package com.tomaszkrystkowiak.biegalizacja;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AppSettings extends Activity {

    EditText set_name;
    EditText set_height;
    EditText set_weight;
    Button settings_save_button;

    public static final String MyPREFERENCES = "Preferences" ;
    public static final String NameS = "nameKey";
    public static final String HeightS = "heightKey";
    public static final String WeightS = "weightKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_settings);

        set_name=(EditText)findViewById(R.id.set_name);
        set_height=(EditText)findViewById(R.id.set_height);
        set_weight=(EditText)findViewById(R.id.set_weight);

        settings_save_button=(Button)findViewById(R.id.settings_save_button);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        settings_save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name  = set_name.getText().toString();
                String height  = set_height.getText().toString();
                String weight  = set_weight.getText().toString();

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(NameS, name);
                editor.putString(HeightS, height);
                editor.putString(WeightS, weight);
                editor.commit();
                Toast.makeText(AppSettings.this,"Saved",Toast.LENGTH_LONG).show();
            }
        });
    }

}
