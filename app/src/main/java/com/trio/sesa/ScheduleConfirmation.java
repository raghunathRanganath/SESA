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
    ImageButton homeButton;
    ImageButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_confirmation);
        Intent scheduleConfirmationIntent = getIntent();

        scheduleID = (EditText)findViewById(R.id.ScheduleID);

        scheduleID.setText(scheduleConfirmationIntent.getStringExtra("scheduleId"));
        new ExecuteTask().execute(scheduleID.getText().toString());

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
            HttpPost httpPost = new HttpPost("http://192.168.43.103:8084/SESA_WS/getSchedule");

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
