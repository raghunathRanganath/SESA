package com.trio.sesa;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText patientName;
    private EditText patientId;
    private Button scheduleButton;
    private Button emergencyButton;
    private ImageButton scanQRLiveButton;
    private static final int SCAN_LIVE_QR_RESULT = 1010;
    ProgressBar progressBar;
    boolean isPatientValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("SESA"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        patientName = (EditText) findViewById(R.id.PatientName);
        patientName.requestFocus();
        patientId = (EditText) findViewById(R.id.PatientId);
        scheduleButton = (Button) findViewById(R.id.ScheduleButton);
        emergencyButton = (Button) findViewById(R.id.emergencyButton);
        scanQRLiveButton = (ImageButton) findViewById(R.id.qrScanButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        scheduleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                scheduleAppointment(patientName.getText().toString(), patientId.getText().toString());
            }
        });

        scanQRLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCodeLive();
            }
        });

        emergencyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                emergencySOS();
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.emergency:
                Intent emergencyReg = new Intent(MainActivity.this, ScanLiveActivity.class);
                emergencyReg.putExtra("PatientName", patientName.getText().toString());
                emergencyReg.putExtra("PatientID", patientId.getText().toString());
                startActivity(emergencyReg);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Function which detects the NFC tap on the phone and launches the app*/
    protected void onNewIntent(Intent nfcIntent) {
        super.onNewIntent(nfcIntent);

        if (nfcIntent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(nfcIntent.getAction())) {
            Parcelable[] rawMessages = nfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessages != null) {
                NdefMessage messages = (NdefMessage) rawMessages[0];
                byte[] payload = messages.getRecords()[0].getPayload();
                char[] patientDetails = new char[payload.length];
                for (int i = 0; i < payload.length; i++)
                    patientDetails[i] = (char) payload[i];
                String pDetails = String.copyValueOf(patientDetails);
                setValuesForScheduleFromNFC(pDetails);
            }
        }
    }

    /*Function which sets the values sent from the NFC scan into the main page of the app*/
    private void setValuesForScheduleFromNFC(String pDetails) {
        patientName.setText(pDetails.substring(9, pDetails.indexOf(";")));
        patientId.setText(pDetails.substring(pDetails.indexOf(";") + 5));
        patientName.requestFocus();
    }

    /*Function which sets the values sent from the QR code intent into the main page of the app*/
    private void setValuesForScheduleFromQR(HashMap patientDetails) {
        if (patientDetails.size() == 2) {
            String pName = patientDetails.get("val0").toString();
            String pID = patientDetails.get("val1").toString();

            patientName.setText(pName.substring(pName.indexOf(":") + 1));
            patientId.setText(pID.substring(pID.indexOf(":") + 1));
        } else {
            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.invalidQRCodeMessage), Toast.LENGTH_LONG).show();
        }
    }

    /*Function which opens the intent to scan a QR code in live mode*/
    private void scanQRCodeLive() {
        Intent scanIntent = new Intent(MainActivity.this, ScanLiveActivity.class);
        startActivityForResult(scanIntent, SCAN_LIVE_QR_RESULT);
    }

    /*Function which opens the intent to start the scheduling workflow*/
    private void scheduleAppointment(String patientName, String patientID) {
        if (!patientName.equals("") && !patientID.equals("")) {
            validatePatient(patientName, patientID);
        } else {
            Toast.makeText(MainActivity.this, "Cannot proceed to Schedule without patient Details", Toast.LENGTH_LONG).show();
        }
    }

    private void validatePatient(String patientName, String patientID) {
        progressBar.setVisibility(View.VISIBLE);
        new ExecuteTask().execute(patientName, patientID);
    }

    @Override
    /* Function which receives the scanned details from QR code scan */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_LIVE_QR_RESULT && resultCode == SCAN_LIVE_QR_RESULT && data != null) {
            HashMap patientDetails = new HashMap();
            patientDetails = (HashMap) data.getSerializableExtra("patientParameters");
            setValuesForScheduleFromQR(patientDetails);
        }
    }

    private void emergencySOS() {
        Intent emergencyIntent = new Intent(MainActivity.this, EmergencySOSActivity.class);
        startActivity(emergencyIntent);
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
                Intent scheduleIntent = new Intent(MainActivity.this, ScheduleActivity.class);
                scheduleIntent.putExtra("PatientName", patientName.getText().toString());
                scheduleIntent.putExtra("PatientID", patientId.getText().toString());
                startActivity(scheduleIntent);
            } else {
                Toast.makeText(MainActivity.this, "Patient Details incorrect, please contact the hospital IT for user creation", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String PostData(String[] values) {
        String s = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
//            InetAddress address = InetAddress.getByName(getString(R.string.host_name));
//            HttpPost httpPost = new HttpPost("http://"+ address.getHostAddress() +":8084/SESA_WS/auth/patientAuth");
            String serverPath = MainActivity.this.getString(R.string.serverIP);
            HttpPost httpPost = new HttpPost(serverPath + "auth/patientAuth");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("pname", values[0]));
            list.add(new BasicNameValuePair("pid", values[1]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            s = readResponse(httpResponse);

        } catch (Exception exception) {
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
}

