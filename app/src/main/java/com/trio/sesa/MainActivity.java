package com.trio.sesa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText patientName;
    private EditText patientId;
    private Button scheduleButton;
    private Button emergencyButton;
    private ImageButton scanQRLiveButton;
    private static final int SCAN_LIVE_QR_RESULT = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            patientName = (EditText)findViewById(R.id.PatientName);
            patientName.requestFocus();
            patientId = (EditText)findViewById(R.id.PatientId);
            scheduleButton = (Button)findViewById(R.id.ScheduleButton);
            emergencyButton = (Button)findViewById(R.id.emergencyButton);
            scanQRLiveButton = (ImageButton)findViewById(R.id.qrScanButton);

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

    private void setValuesForSchedule(HashMap patientDetails) {
        if(patientDetails.size() == 2) {
            String pName = patientDetails.get("val0").toString();
            String pID = patientDetails.get("val1").toString();

            patientName.setText(pName.substring(pName.indexOf(":")+1));
            patientId.setText(pID.substring(pID.indexOf(":")+1));
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.invalidQRCodeMessage) , Toast.LENGTH_LONG).show();
        }
    }

    private void scanQRCodeLive() {
        Intent scanIntent = new Intent(MainActivity.this, ScanLiveActivity.class);
        startActivityForResult(scanIntent, SCAN_LIVE_QR_RESULT);
    }

    private void scheduleAppointment(String patientName, String patientID) {
            Intent scheduleIntent = new Intent(MainActivity.this, ScheduleActivity.class);
            scheduleIntent.putExtra("PatientName", patientName.toString());
            startActivity(scheduleIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SCAN_LIVE_QR_RESULT && resultCode == SCAN_LIVE_QR_RESULT && data != null) {
                HashMap patientDetails = new HashMap();
                patientDetails = (HashMap) data.getSerializableExtra("patientParameters");
                setValuesForSchedule(patientDetails);
        }
    }
}
