package com.trio.sesa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.List;

public class SlotSelectionActivity extends AppCompatActivity {

    EditText datePicker;
    EditText time;
    DatePickerDialog datePickerDialog;
    Button confirmButton;
    EditText commentsText;

    String physicianName = "";
    String specialtyName = "";
    String patientName = "";
    String patientID = "";
    String scheduleDate = "";
    String comments = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_selection);
        getSupportActionBar().setTitle("SESA"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent slotIntent = getIntent();
        confirmButton = (Button)findViewById(R.id.confirmScheduleButton);
        confirmButton.setEnabled(false);

        commentsText = (EditText)findViewById(R.id.commentsEditText);

        patientName = slotIntent.getStringExtra("PatientName");
        patientID = slotIntent.getStringExtra("PatientID");

        if(!slotIntent.getStringExtra("PhysicianName").equals("")) {
            physicianName = slotIntent.getStringExtra("PhysicianName");
        } else {
            specialtyName = slotIntent.getStringExtra("SpecialtyName");
        }

        datePicker = (EditText)findViewById(R.id.datePickEditText);
        time = (EditText)findViewById(R.id.timePickEditText);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(SlotSelectionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datePicker.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);
                                time.callOnClick();
                                confirmButton.setEnabled(true);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

                if(datePicker.getText().equals("")) {
                    confirmButton.setEnabled(false);
                } else {
                    confirmButton.setEnabled(true);
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                final TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SlotSelectionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedMinute < 10)
                            time.setText(selectedHour + ":" + "0" + selectedMinute + ":00");
                        else
                            time.setText(selectedHour + ":" + selectedMinute + ":00");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

                if(time.getText().equals("")) {
                    confirmButton.setEnabled(false);
                } else {
                    confirmButton.setEnabled(true);
                }
            }
        });
        datePicker.callOnClick();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleDate = datePicker.getText() + " " + time.getText();
                comments = commentsText.getText().toString();
                new ExecuteTask().execute(patientName, patientID, physicianName, specialtyName, scheduleDate, comments);
            }
        });
    }

    class ExecuteTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String res = PostData(params);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(SlotSelectionActivity.this, "Execution completed, Schedule created", Toast.LENGTH_SHORT).show();
            Intent scheduleConfirmedIntent = new Intent(SlotSelectionActivity.this, ScheduleConfirmation.class);
            scheduleConfirmedIntent.putExtra("scheduleId", result);
            scheduleConfirmedIntent.putExtra("phySpecDetail", physicianName.equals("") ? specialtyName : physicianName );
            scheduleConfirmedIntent.putExtra("patientName", patientName);
            scheduleConfirmedIntent.putExtra("scheduleDateTime", scheduleDate);

            startActivity(scheduleConfirmedIntent);

        }
    }

    public String PostData(String[] values) {
        String s = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            String serverPath = SlotSelectionActivity.this.getString(R.string.serverIP);
            HttpPost httpPost = new HttpPost(serverPath + "createSchedule");

            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("pname", values[0]));
            list.add(new BasicNameValuePair("pid", values[1]));
            list.add(new BasicNameValuePair("physicianName", values[2]));
            list.add(new BasicNameValuePair("specialtyName", values[3]));
            list.add(new BasicNameValuePair("scheduleDate", values[4]));
            list.add(new BasicNameValuePair("comments", values[5]));
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
