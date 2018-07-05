package com.trio.sesa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class ScheduleActivity extends AppCompatActivity {

    private EditText patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Intent scheduleIntent = getIntent();
        patientName = (EditText)findViewById(R.id.PatientName);

        String name = scheduleIntent.getStringExtra("PatientName");
        patientName.setText(name);
    }
}
