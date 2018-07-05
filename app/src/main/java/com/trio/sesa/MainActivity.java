package com.trio.sesa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText patientName;
    private EditText patientId;
    private Button scheduleButton;
    private Button emergencyButton;
    private Button scanQRLiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        patientName = (EditText)findViewById(R.id.PatientName);
        patientId = (EditText)findViewById(R.id.PatientId);
        scheduleButton = (Button)findViewById(R.id.ScheduleButton);
        emergencyButton = (Button)findViewById(R.id.emergencyButton);
        scanQRLiveButton = (Button)findViewById(R.id.ScanRealTimeButton);

        scheduleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                scheduleAppointment(patientName.getText().toString(),patientId.getText().toString());
            }
        });

        scanQRLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCodeLive();
            }
        });
    }

    private void scanQRCodeLive() {
        Intent scanIntent = new Intent(MainActivity.this, ScanLiveActivity.class);
        startActivity(scanIntent);
    }

    private void scheduleAppointment(String patientName, String patientID) {
            Intent scheduleIntent = new Intent(MainActivity.this, ScheduleActivity.class);
            scheduleIntent.putExtra("PatientName", patientName.toString());
            startActivity(scheduleIntent);
    }
}
