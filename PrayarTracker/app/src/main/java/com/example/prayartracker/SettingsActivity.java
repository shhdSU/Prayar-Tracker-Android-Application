package com.example.prayartracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch allowNotifications = (Switch) findViewById(R.id.allowNotif);
        final Switch silentModeSwitch = (Switch) findViewById(R.id.silentModeSwitch);
        final LinearLayout linearLayout = (LinearLayout) findViewById((R.id.linearLayout));
        final EditText minutesInput = (EditText) findViewById((R.id.editTextNumber2));
        Button saveBtn = (Button) findViewById((R.id.SaveBtn));
        silentModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(silentModeSwitch.isChecked()){
                    int numMinutes = Integer.parseInt(minutesInput.getText().toString());

                }
                Intent intent = new Intent(getApplicationContext(),HomeScreenActivity.class);
                startActivity(intent);
            }
        });


    }
}