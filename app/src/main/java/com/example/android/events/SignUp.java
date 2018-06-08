package com.example.android.events;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

public class SignUp extends AppCompatActivity {
    Button btnReg;
    EditText edtUser, edtPass, edtConfPass, edtEmail;
    String username,email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //initialization of all editText

        edtUser=(EditText)findViewById(R.id.edtUsername);
        edtPass=(EditText)findViewById(R.id.edtPass);
        edtConfPass=(EditText)findViewById(R.id.edtConfirmPass);
        edtEmail=(EditText)findViewById(R.id.edtEmail);
        //Initialization of Register Button
        btnReg=(Button)findViewById(R.id.button1);

        //Registration button functionality
//        btnReg.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//
//
//
//        }
//    });

    }
        public void senddatatoserver(View v) {

            if(edtUser.getText().toString().length()==0){
                edtUser.setError("Username is Required");
                edtUser.requestFocus();
            }
            if(edtPass.getText().toString().length()==0){
                edtPass.setError("Password not entered");
                edtPass.requestFocus();
            }
            if(edtConfPass.getText().toString().length()==0){
                edtConfPass.setError("Please confirm password");
            }
            if(!edtPass.getText().toString().equals(edtConfPass.getText().toString())){
                edtConfPass.setError("Password Not matched");
                edtConfPass.requestFocus();
            }
            if(edtPass.getText().toString().length()<8){
                edtPass.setError("Password should be atleast of 8 charactors");
                edtPass.requestFocus();
            }
            else {
                //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                username = edtUser.getText().toString();//store the data the user entered in te edit box
                email = edtEmail.getText().toString();//store the data the user entered in te edit box
                password = edtPass.getText().toString();//store the data the user entered in te edit box
                JSONObject post_dict = new JSONObject(); //create a new JSON object that will be used to send data

                try {
                    post_dict.put("username", username);//put the data into the json object --> 'key','value' structure
                    post_dict.put("email", email);//put the data into the json object --> 'key','value' structure
                    post_dict.put("password", password);//put the data into the json object --> 'key','value' structure

                } catch (JSONException e) {
                    e.printStackTrace();//print exception, if there is one
                }
                if (post_dict.length() > 0) {
                    new SendDataToServer().execute(String.valueOf(post_dict));//call to async task with json object passed
                    Log.i("Json:", post_dict.toString());

                }
            }
            //function in the activity that corresponds to the layout button

        }

    @SuppressLint("StaticFieldLeak")
    class SendDataToServer extends AsyncTask <String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = "";//used to store the response from the server
            String JsonDATA = params[0]; //as parameter can take many values, get first element
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL("http://group21flaskaws.eu-west-2.elasticbeanstalk.com/newuser"); //URL to a specific endpoint (sign-up in this case)
                //IP: 35.178.140.179
                urlConnection = (HttpURLConnection) url.openConnection();//make a HTTP connection to the URL
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");//define which request is being made. POST request, in this case.
                //how long to wait for a connection to be made
                urlConnection.setReadTimeout(10000); //miliseconds
                urlConnection.setConnectTimeout(15000); //milliseconds
                //set headers
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                //create output stream and send JSON data
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();//close writer

                //if connection is made successfully
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
          extractJson(response);
        }

    }
    //Take user to login screen if account is created or display message as to why the account was not created
    private void extractJson(String responseJson){
        if(TextUtils.isEmpty(responseJson)){
            Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                JSONObject baseJsonResponse = new JSONObject(responseJson);
                String response = baseJsonResponse.getString("response");
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                if (response.equalsIgnoreCase("Account Created")) {
                    Log.e("Test2", "Activity change");
                    Intent i = new Intent(this, LogIn.class);
                    startActivity(i);
                }
            } catch (JSONException e) {
                Log.e("Error", "Problems parsing JSON results.");
            }
        }

    }
}








