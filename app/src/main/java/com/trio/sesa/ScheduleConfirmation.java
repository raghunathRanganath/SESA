package com.trio.sesa;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ScheduleConfirmation extends AppCompatActivity {

    EditText scheduleID;
    EditText phySpecDetail;
    EditText patientName;
    EditText scheduleDateTime;
    EditText additionalInfo;

    ImageButton homeButton;
    ImageButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_confirmation);
        getSupportActionBar().setTitle("SESA"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent scheduleConfirmationIntent = getIntent();

        scheduleID = (EditText)findViewById(R.id.ScheduleID);
        phySpecDetail = (EditText)findViewById(R.id.physpecBox);
        patientName = (EditText)findViewById(R.id.patientNameBox);
        scheduleDateTime = (EditText)findViewById(R.id.scheduleDateText);
        additionalInfo = (EditText)findViewById(R.id.AdditionalInfoBox);

        scheduleID.setText(scheduleConfirmationIntent.getStringExtra("scheduleId"));
        scheduleID.setEnabled(false);
        phySpecDetail.setText(scheduleConfirmationIntent.getStringExtra("phySpecDetail"));
        phySpecDetail.setEnabled(false);
        patientName.setText(scheduleConfirmationIntent.getStringExtra("patientName"));
        patientName.setEnabled(false);
        scheduleDateTime.setText(scheduleConfirmationIntent.getStringExtra("scheduleDateTime"));
        scheduleDateTime.setEnabled(false);
        String scheduleDateTime = scheduleConfirmationIntent.getStringExtra("scheduleDateTime");
        String scheduleDate = scheduleDateTime.substring(0,scheduleDateTime.indexOf(" "));
        String scheduleTime = scheduleDateTime.substring(scheduleDateTime.indexOf(" "));
        String finalComment = ScheduleConfirmation.this.getString(R.string.scheduleConfirmationMessage).replace("@date@",scheduleDate);
        finalComment = finalComment.replace("@time@",scheduleTime);
        String hospitalAddress = ScheduleConfirmation.this.getString(R.string.hospitalAddress);
        additionalInfo.setText(finalComment + "\n" + hospitalAddress);
        additionalInfo.setEnabled(false);

//        new ExecuteTask().execute(scheduleID.getText().toString());

        homeButton = (ImageButton)findViewById(R.id.homeButton);
        logoutButton = (ImageButton)findViewById(R.id.logoutButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ScheduleConfirmation.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
    }

    public void onBackPressed() {
        Intent mainIntent = new Intent(ScheduleConfirmation.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private class ExecuteTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String res = PostData(params);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ScheduleConfirmation.this, result, Toast.LENGTH_SHORT).show();
//            Intent scheduleConfirmedIntent = new Intent(SlotSelectionActivity.this, ScheduleConfirmation.class);
//            scheduleConfirmedIntent.putExtra("scheduleId", result);
//            startActivity(scheduleConfirmedIntent);

        }
    }

    public String PostData(String[] values) {
        String s = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            String serverPath = ScheduleConfirmation.this.getString(R.string.serverIP);
            HttpPost httpPost = new HttpPost(serverPath + "getSchedule");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("sid", values[0]));
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
            return_text = sb.toString().substring(sb.length()-4);
        } catch (Exception e) {

        }
        return return_text;
    }
}
