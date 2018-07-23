package com.trio.sesa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class EmergencyRegistration extends AppCompatActivity {

    private EditText edtPtContactNo,edEmgContactPersonName, edtEmgContactNo, edtPtAge, edtPtBloodGrp;
    private Button btnRegister, btnCancel;
    private ProgressBar progressBar;
    private String pid;
    ImageButton homeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_registration);

        Intent emergencyRegister = getIntent();
        final String pname = emergencyRegister.getStringExtra("PatientName");
        pid = emergencyRegister.getStringExtra("PatientID");
        validatePatientDetails(pname, pid);
        edtPtContactNo = (EditText) findViewById(R.id.edtPtContactNo);
        edtPtContactNo.requestFocus();
        edEmgContactPersonName = (EditText) findViewById(R.id.edEmgContactPersonName);
        edtEmgContactNo = (EditText) findViewById(R.id.edtEmgContactNo);
        edtPtAge = (EditText) findViewById(R.id.edtPtAge);
        edtPtBloodGrp = (EditText) findViewById(R.id.edtPtBloodGrp);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                registerEmergencyRecord(edtPtContactNo.getText().toString(), edEmgContactPersonName.getText().toString(), edtEmgContactNo.getText().toString());
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(EmergencyRegistration.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(EmergencyRegistration.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
    void registerEmergencyRecord(String edtPtContactNo, String edEmgContactPersonNameText, String edtEmgContactNoText){
        if (!edtPtContactNo.equals("") && !edEmgContactPersonNameText.equals("") && !edtEmgContactNoText.equals("")) {
            validateRecords(edtPtContactNo, edEmgContactPersonNameText, edtEmgContactNoText);
        } else {
            Toast.makeText(EmergencyRegistration.this, "Please fill all required fields", Toast.LENGTH_LONG).show();
        }

    }

    private void validateRecords(String edtPtContactNo, String edEmgContactPersonNameText, String edtEmgContactNoText) {
        progressBar.setVisibility(View.VISIBLE);
        new ExecuteTask().execute(edtPtContactNo, edEmgContactPersonNameText, edtEmgContactNoText, edtPtAge.getText().toString(), edtPtBloodGrp.getText().toString(), pid);
    }
    class ExecuteTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String res = PostData(params);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if(result.equals("SESA-001")) {
                Intent mainIntent = new Intent(EmergencyRegistration.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            } else {
                Toast.makeText(EmergencyRegistration.this, "Not able to register emergency details", Toast.LENGTH_LONG).show();
            }
        }
    }
    public String PostData(String[] values) {
        String s = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            String serverPath = EmergencyRegistration.this.getString(R.string.serverIP);
            HttpPost httpPost = new HttpPost(serverPath + "emgRegistration");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("edtPtContactNo", values[0]));
            list.add(new BasicNameValuePair("edEmgContactPersonName", values[1]));
            list.add(new BasicNameValuePair("edtEmgContactNo", values[2]));
            list.add(new BasicNameValuePair("edtPtAge", values[3]));
            list.add(new BasicNameValuePair("edtPtBloodGrp", values[4]));
            list.add(new BasicNameValuePair("pid", values[5]));

            httpPost.setEntity(new UrlEncodedFormEntity(list));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            s = readResponse(httpResponse);

        } catch (Exception exception) {
            Log.e("Error in Registratino", exception.getMessage());
        }
        return s;


    }

    public String readResponse(HttpResponse res) {
        InputStream is = null;
        String return_text = "";
        try {
            is = res.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return_text = sb.toString().split("\b")[1];
        } catch (Exception e) {

        }
        return return_text;
    }

    private void validatePatientDetails(String patientName, String patientID) {
        if (!patientName.equals("") && !patientID.equals("")) {
            validatePatient(patientName, patientID);
        } else {
            Toast.makeText(EmergencyRegistration.this, "Patients details not found", Toast.LENGTH_LONG).show();
            disabledUI();
        }
    }

    private void validatePatient(String patientName, String patientID) {
        progressBar.setVisibility(View.VISIBLE);
        new ExecuteTaskValidation().execute(patientName, patientID);
    }
    class ExecuteTaskValidation extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String res = PostPatientData(params);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if(!result.equals("SESA-001")) {
                Toast.makeText(EmergencyRegistration.this, "Patient Details Not found", Toast.LENGTH_LONG).show();
                disabledUI();
            }
        }
    }

    private void disabledUI() {
        edtPtContactNo.setEnabled(false);
        edEmgContactPersonName.setEnabled(false);
        edtEmgContactNo.setEnabled(false);
        edtPtAge.setNestedScrollingEnabled(false);
        edtPtBloodGrp.setNestedScrollingEnabled(false);
        btnRegister.setEnabled(false);
    }

    public String PostPatientData(String[] values) {
        String s = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
//            InetAddress address = InetAddress.getByName(getString(R.string.host_name));
//            HttpPost httpPost = new HttpPost("http://"+ address.getHostAddress() +":8084/SESA_WS/auth/patientAuth");
            String serverPath = EmergencyRegistration.this.getString(R.string.serverIP);
            HttpPost httpPost = new HttpPost(serverPath + "auth/patientAuth");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("pname", values[0]));
            list.add(new BasicNameValuePair("pid", values[1]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            s = readResponse(httpResponse);

        } catch (Exception exception) {
            Log.d("Emergency Registration:", exception.getMessage());
        }
        return s;


    }
}
