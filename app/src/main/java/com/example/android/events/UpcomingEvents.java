package com.example.android.events;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.events.models.EventModel;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class UpcomingEvents extends Fragment {

    private static final String TAG = "UpcomingEvents";
    private int ListPosition;
    //private TextView EventJsonText;
    private ListView lvEvents;
    List<EventModel> eventModelList;
    EventAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upcoming_events,container,false);
       // EventJsonText = (TextView)view.findViewById(R.id.UpcomingEventsText);
        lvEvents = (ListView) view.findViewById(R.id.lvEvents);
        new SendDataToServer().execute("http://group21flaskaws.eu-west-2.elasticbeanstalk.com/events/upcomingevents");
        return view;
    }
    @SuppressLint("StaticFieldLeak")
    class SendDataToServer extends AsyncTask<String,Void,String> {

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
            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
        }
        else {

            try {
               // StringBuffer finalJson = new StringBuffer();
                JSONObject baseJsonResponse = new JSONObject(responseJson);
                JSONArray parentArray = baseJsonResponse.getJSONArray("events");
                eventModelList = new ArrayList<>();
                for (int i =0; i < parentArray.length();i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    Log.i("Post Response:", responseJson);
                    EventModel eventModel = new EventModel();
                    eventModel.setName(finalObject.getString("event_name"));

                    String dbdate = formatDate(finalObject.getString("event_date"));
                    eventModel.setDate(dbdate);
                    eventModel.setTime(finalObject.getString("event_time"));
                    eventModel.setId(finalObject.getInt("id"));
                    eventModelList.add(eventModel);

//                    String eventName = finalObject.getString("event_name");
//                    Log.i("Name",eventName);
//
//                    String eventTime = finalObject.getString("event_time");
//                    Log.i("Time",eventTime);

//                    String eventDate = finalObject.getString("event_date");
//                    Log.i("Date", eventDate);

//                    int eventId = finalObject.getInt("id");
//                    Log.i("Id",Integer.toString(eventId));

                    //finalJson.append(eventName + " - " + eventDate + " - " + eventTime + "\n");
                }
                adapter = new EventAdapter(getActivity().getApplicationContext(), R.layout.eventlistrow, eventModelList);
                lvEvents.setAdapter(adapter);
              // EventJsonText.setText(finalJson.toString());

            } catch (JSONException e) {
                Log.e("Error", "Problems parsing JSON results.");
            }
        }

    }

    public class EventAdapter extends ArrayAdapter{
        private List<EventModel> eventModelList;
        private int resource;
        private LayoutInflater inflater;
        public EventAdapter(@NonNull Context context, int resource, @NonNull List<EventModel> objects) {
            super(context, resource, objects);
            eventModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(resource, null);
            }
            //TextView eventName;
//            TextView eventDate;
//            TextView eventTime;


           TextView eventName = convertView.findViewById(R.id.ListEventName);
           TextView eventDate = convertView.findViewById(R.id.ListEventDate);
           TextView eventTime = convertView.findViewById(R.id.ListEventTime);
           ImageButton deleteEvent = convertView.findViewById(R.id.deletebutton);
           ImageButton editEvent = convertView.findViewById(R.id.editButton);
            eventName.setText(eventModelList.get(position).getName());
            eventDate.setText("Date: " + eventModelList.get(position).getDate());
            eventTime.setText("Time: " + eventModelList.get(position).getTime());

            deleteEvent.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ListPosition = position;

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Delete Event")
                            .setMessage("Are you sure you want to delete this event?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    String eventId = Integer.toString(eventModelList.get(ListPosition).getId());
                                    new DeleteEventFromServer().execute("http://group21flaskaws.eu-west-2.elasticbeanstalk.com/delete/event/"+eventId);
                                   //Toast.makeText(getActivity().getApplicationContext(),"Deleting..." + ListPosition + "id: "+ eventId ,Toast.LENGTH_LONG).show();
                                   notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .show();

                }
            });
            editEvent.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(),detailsEventDB.class);
                    i.putExtra("EventId", Integer.toString(eventModelList.get(position).getId()));//used to EventID to other activity.
                    startActivity(i);
                }
            });

            return convertView;
        }

    }

    @SuppressLint("StaticFieldLeak")
    class DeleteEventFromServer extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = "";

            HttpURLConnection urlConnection = null;
            //BufferedReader reader = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setReadTimeout(60000); //(miliseconds) 60 secs
                urlConnection.setConnectTimeout(15000); //milliseconds
//                urlConnection.setRequestProperty("Content-Type", "application/json");
//                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();


                inputStream = urlConnection.getInputStream();
                if(urlConnection.getResponseCode()==200) {
                    JsonResponse = readFromStream(inputStream);
                }
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
            extractJsonResponse(response);

        }

    }

    private void extractJsonResponse(String responseJson){
        if(TextUtils.isEmpty(responseJson)){
            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                JSONObject baseJsonResponse = new JSONObject(responseJson);
                String response = baseJsonResponse.getString("response");
                Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_LONG).show();
                if (response.equalsIgnoreCase("Event Deleted")) {
                    adapter.remove(adapter.getItem(ListPosition));
                    //adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                Log.e("Error", "Problems parsing JSON results.");
            }
        }

    }

    public static String formatDate(String inDate) {
        SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat outSDF = new SimpleDateFormat("dd-mm-yyyy");

        String outDate = "";
        if (inDate != null) {
            try {
                Date date = inSDF.parse(inDate);
                outDate = outSDF.format(date);
            } catch (ParseException ex){
            }
        }
        return outDate;
    }


}
