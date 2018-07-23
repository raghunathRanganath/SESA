package com.trio.sesa;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private EditText patientName;
    private RadioGroup radioGroup;
    private Button scheduleContinueButton;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().setTitle("SESA"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent scheduleIntent = getIntent();
        patientName = (EditText)findViewById(R.id.PatientName);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        scheduleContinueButton = (Button) findViewById(R.id.scheduleContinueButton);

        final String pname = scheduleIntent.getStringExtra("PatientName");
        final String pid = scheduleIntent.getStringExtra("PatientID");
        patientName.setText(pname);

        final Spinner doctorDropDown = findViewById(R.id.doctorList);
        final Spinner specialtyDropDown = findViewById(R.id.specialtyList);

        doctorDropDown.setEnabled(false);
        specialtyDropDown.setEnabled(false);
        scheduleContinueButton.setEnabled(false);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.physicianRB) {
                    doctorDropDown.setEnabled(true);
                    specialtyDropDown.setEnabled(false);
                    scheduleContinueButton.setEnabled(true);
                } else if(i == R.id.specialtyRB) {
                    specialtyDropDown.setEnabled(true);
                    doctorDropDown.setEnabled(false);
                    scheduleContinueButton.setEnabled(true);
                }
            }
        });

        String[] tempDoctorList = new String[]{"Dr. Ramakanth Desai", "Dr. Banerji B", "Dr. Victor Dsouza", "Dr. Kishore", "Dr. Siddharth", "Dr. Ramadevi"};
//        new ExecuteTask().execute();
        String[] tempSpecialtyList = new String[]{"Cardiology", "Dermatology", "General Consultant", "Neurology", "Obstetrics", "Gyeanacology"};
        ArrayAdapter<String> doctorSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tempDoctorList);
        ArrayAdapter<String> specialtySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tempSpecialtyList);
        doctorDropDown.setAdapter(doctorSpinnerAdapter);
        specialtyDropDown.setAdapter(specialtySpinnerAdapter);

        scheduleContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent slotSelectionIntent = new Intent(ScheduleActivity.this, SlotSelectionActivity.class);
                radioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                if(radioButton.getText().equals("Physician")) {
                    slotSelectionIntent.putExtra("PhysicianName", doctorDropDown.getSelectedItem().toString());
                } else {
                    slotSelectionIntent.putExtra("PhysicianName", "");
                }

                if(radioButton.getText().equals("Specialty")) {
                    slotSelectionIntent.putExtra("SpecialtyName", specialtyDropDown.getSelectedItem().toString());
                } else {
                    slotSelectionIntent.putExtra("SpecialtyName", "");
                }
                slotSelectionIntent.putExtra("PatientName", pname);
                slotSelectionIntent.putExtra("PatientID", pid);
                startActivity(slotSelectionIntent);
            }
        });


    }

//    class ExecuteTask extends AsyncTask<String, Integer, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            String res = PostData();
//            return res;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if(true) {
//                Toast.makeText(ScheduleActivity.this, "Success", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(ScheduleActivity.this, "Patient Details incorrect, please contact the hospital IT for user creation", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

//    public String PostData() {
//        String s = "";
//        try {
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost("http://192.168.0.102:8084/SESA_WS/physicianList");
//
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//
//            HttpEntity httpEntity = httpResponse.getEntity();
//            s = readResponse(httpResponse);
//
//        } catch (Exception exception) {
//        }
//        return s;
//
//
//    }

//    public String readResponse(HttpResponse res) {
//        InputStream is = null;
//        String return_text = "";
//        try {
//            is = res.getEntity().getContent();
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
//            String line = "";
//            StringBuffer sb = new StringBuffer();
//            while ((line = bufferedReader.readLine()) != null) {
//                sb.append(line);
//            }
//            return_text = sb.toString().split("\b")[1];
//        } catch (Exception e) {
//
//        }
//        return return_text;
//    }
}
