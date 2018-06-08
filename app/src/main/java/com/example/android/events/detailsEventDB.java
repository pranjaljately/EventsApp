package com.example.android.events;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.events.models.EventModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;


public class detailsEventDB extends AppCompatActivity implements View.OnClickListener {
    EditText eventname,event_address;
    EditText in_date, in_time;
    Button btn_date, btn_time;
    StringBuilder dateSB;
    String EventName, EventTime, EventDate, EventAddress;
    String eventId;


    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    int PLACE_PICKER_REQUEST = 1;
    private String address;
    private GeoDataClient mGeoDataClient;
    private GoogleApiClient mGoogleApiClient;
    public String placeId = "849VCWC9+92Q7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        eventname = (EditText)findViewById(R.id.eventname);
        event_address = findViewById(R.id.Event_Address);
//        date = (EditText)findViewById(R.id.in_date);
//        time = (EditText)findViewById(R.id.in_time);

        in_date = (EditText) findViewById(R.id.in_date);
        in_time = (EditText) findViewById(R.id.in_time);

        btn_date = (Button) findViewById(R.id.btn_date);
        btn_time = (Button) findViewById(R.id.btn_time);

        btn_time.setOnClickListener(this);
        btn_date.setOnClickListener(this);

        Button shoppinglist =  (Button) findViewById(R.id.itemlist);
       // Button location = (Button) findViewById(R.id.location);
        Button eventLocation = (Button) findViewById(R.id.location);
        dateSB = new StringBuilder("");
        Intent idIntent = getIntent();
        if (idIntent.hasExtra("EventId")){
            Toast.makeText(this,"id: "+ idIntent.getStringExtra("EventId") ,Toast.LENGTH_LONG).show();
            eventId = idIntent.getStringExtra("EventId");
            new GetDataFromServer().execute("http://group21flaskaws.eu-west-2.elasticbeanstalk.com/event/"+eventId);
          }


        shoppinglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(detailsEventDB.this, LIST.class);
                startActivity(intent);
            }
        });

        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEventLocationButtonClick();
            }
        });

//        Intent myIntent = getIntent();
//        if (myIntent.hasExtra("EventAddress")){
//            TextView selectedAddress = (TextView)findViewById(R.id.Event_Address);
//            selectedAddress.setText(myIntent.getStringExtra("EventAddress")); }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_date:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);



                datePickerDialog = new DatePickerDialog(detailsEventDB.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    in_date.setText(dayOfMonth + "-" + (month+1) + "-" + year);
                        dateSB.setLength(0);
                        dateSB.append(year).append("-").append(month+1).append("-").append(dayOfMonth);
                    }
                }, year, month, day);
                datePickerDialog.show();
            break;
            case R.id.btn_time:
                Calendar calendar1 = Calendar.getInstance();
                int hour = calendar1.get(Calendar.HOUR);
                int min = calendar1.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(detailsEventDB.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    in_time.setText(hourOfDay + ":" + minute);
                    }
                }, hour, min, true);
                timePickerDialog.show();
                break;

        }

    }

    public void onEventLocationButtonClick(){
//        Bundle savedInstanceState
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                //String toastMsg = String.format("Location: %s", place.getName());
                placeId = place.getId();
                String location = place.getAddress().toString();
                String toastMsg = "Location:" + location;
//              Log.d("test",placeId);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//                Intent i = new Intent(this, details.class);
//                i.putExtra("EventAddress", location);//used to pass address to other activity (event details)
//                startActivity(i);
                TextView selectedAddress = (TextView)findViewById(R.id.Event_Address);
                selectedAddress.setText(location);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.e("menu", Integer.toString(id));
        if(id == R.id.action_save){
            //validation for name, date and time

            if(eventname.getText().toString().length()==0){
                eventname.setError("Please enter a name");
                eventname.requestFocus();
            }

            if(in_date.getText().toString().length()==0){
                in_date.setError("Please enter date.");
                in_date.requestFocus();
            }
            else {
                EventDate = dateSB.toString();
            }

            if(in_time.getText().toString().length()==0){
                in_time.setError("Please enter time.");
                in_time.requestFocus();
            }
            else {
                EventName = eventname.getText().toString();
                EventTime = in_time.getText().toString();
                EventAddress = event_address.getText().toString();
                JSONObject post_dict = new JSONObject();

                try {
                    post_dict.put("event_name", EventName);
                    post_dict.put("event_date", EventDate);
                    post_dict.put("event_time", EventTime);
                    post_dict.put("event_location", EventAddress);
                    post_dict.put("event_description", "SaveEventTest");
                    post_dict.put("event_type","testtype");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (post_dict.length() > 0) {
                   new SendDataToServer().execute(String.valueOf(post_dict));
                    Log.i("Json:", post_dict.toString());
                    //call to async class
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    class GetDataFromServer extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = "";

            HttpURLConnection urlConnection = null;
            //BufferedReader reader = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(60000); //(miliseconds) 60 secs
                urlConnection.setConnectTimeout(15000); //milliseconds
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();


                inputStream = urlConnection.getInputStream();
                JsonResponse = readFromStream(inputStream);
//response data
                Log.i("Response: ", JsonResponse);
//send to post execute

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return JsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException{
            StringBuilder output = new StringBuilder();
            if(inputStream != null){
                InputStreamReader inputStreamReader =
                        new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while(line !=null){
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            extractJson(response);

            // extractJson(response);
        }

    }

    private void extractJson(String responseJson){
        if(TextUtils.isEmpty(responseJson)){
            Toast.makeText(this, "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
        }
        else {

            try {
                // StringBuffer finalJson = new StringBuffer();
                JSONObject baseJsonResponse = new JSONObject(responseJson);
                JSONArray parentArray = baseJsonResponse.getJSONArray("event");


                    JSONObject finalObject = parentArray.getJSONObject(0);
                    Log.i("Post Response:", responseJson);

                    eventname.setText(finalObject.getString("event_name"));
                    in_date.setText(finalObject.getString("event_date"));
                    in_time.setText(finalObject.getString("event_time"));
                    event_address.setText(finalObject.getString("event_location"));
                    dateSB = dateSB.append(finalObject.getString("event_date"));

                // EventJsonText.setText(finalJson.toString());

            } catch (JSONException e) {
                Log.e("Error", "Problems parsing JSON results.");
            }
        }

    }
    @SuppressLint("StaticFieldLeak")
    class SendDataToServer extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = "";
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            //BufferedReader reader = null;
            InputStream inputStream = null;
            try {
                URL url = new URL("http://group21flaskaws.eu-west-2.elasticbeanstalk.com/update/event/"+eventId);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("PUT");
//                urlConnection.setReadTimeout(10000); //miliseconds
//                urlConnection.setConnectTimeout(15000); //milliseconds
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();

//input stream

                if(urlConnection.getResponseCode()==200) {
                    //  InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                    inputStream = urlConnection.getInputStream();
                    JsonResponse = readFromStream(inputStream);
//response data
                    Log.e("Response: ", JsonResponse);
//send to post execute
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return JsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException{
            StringBuilder output = new StringBuilder();
            if(inputStream != null){
                InputStreamReader inputStreamReader =
                        new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while(line !=null){
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


        @Override
        protected void onPostExecute(String response) {
            // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            Log.i("Post Response:", response);
            extractJsonResponse(response);
        }

    }

    private void extractJsonResponse(String responseJson){
        if(TextUtils.isEmpty(responseJson)){
            Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                JSONObject baseJsonResponse = new JSONObject(responseJson);
                String response = baseJsonResponse.getString("response");
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                if (response.equalsIgnoreCase("Event Updated")) {
                    Log.e("Test2", "Activity change");
                    Intent i = new Intent(this, activity_upcoming_events.class);
                    startActivity(i);
                }
            } catch (JSONException e) {
                Log.e("Error", "Problems parsing JSON results.");
            }
        }

    }



}
