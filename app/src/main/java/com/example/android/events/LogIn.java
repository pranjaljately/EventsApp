package com.example.android.events;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaCas;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class LogIn extends AppCompatActivity {
    Button login;
    EditText password, username;
    SharedPreferences sp ;


   // String usernameJson, passwordJson;
//    String username = "admin" ;
//    String password = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        login = (Button)findViewById(R.id.button2);
        password = (EditText)findViewById(R.id.EditText);
        username = (EditText)findViewById(R.id.EditText2);

        if (sp.getBoolean("logged",false))
        {
//            SharedPreferences.Editor e=sp.edit();
//            e.clear();
//            e.commit();
            activity();

           }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(username.getText().toString().length()==0){
                    username.setError("Please enter your username.");
                    username.requestFocus();
                }

                if(password.getText().toString().length()==0){
                    password.setError("Please enter your password.");
                    password.requestFocus();
                }
                else if ((password.getText().toString().length()!=0) && (username.getText().toString().length()!=0)){

                    JSONObject post_dict = new JSONObject();

                    try {
                        post_dict.put("username", username.getText().toString());
                        post_dict.put("password", password.getText().toString());

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
        });


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
                //Create a default system-wide CookieManager
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);

                //Open a connection for the given URL
                URL url = new URL("http://group21flaskaws.eu-west-2.elasticbeanstalk.com/login");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
               // urlConnection.setConnectTimeout(15000); //milliseconds
                //urlConnection.getContent();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                //Get CookieStore which is the default internal in-memory
                CookieStore cookieStore = cookieManager.getCookieStore();

                //Retrieve all stored HttpCookies from CookieStore
                List<HttpCookie> cookies = cookieStore.getCookies();

//                //Iterate HttpCookie object
//                for (HttpCookie ck : cookies) {
//                    //Get the cookie name
//                    System.out.println("Cookie name: " + ck.getName());
//
//                    //Get the domain set for the cookie
//                    System.out.println("Domain: " + ck.getDomain());
//
//                    //Get the max age of the cookie
//                    System.out.println("Max age: " + ck.getMaxAge());
//
//                    //Gets the value of the cookie
//                    System.out.println("Cookie value: " + ck.getValue());
//
//                }


//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();

//input stream
                if(urlConnection.getResponseCode()==200) {
                    inputStream = urlConnection.getInputStream();
                    JsonResponse = readFromStream(inputStream);
//response data
                    Log.i("Response: ", JsonResponse);
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
                if (response.equalsIgnoreCase("You are now logged in!")) {
                    Log.e("Test2", "Activity change");
                    Intent i = new Intent(this, activity_upcoming_events.class);
                  sp.edit().putBoolean("logged",true).apply();
                    startActivity(i);
                }
            } catch (JSONException e) {
                Log.e("Error", "Problems parsing JSON results.");
            }
        }
    }

    public void SignUp(View view){
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }
    public void activity(){
        Intent i = new Intent(this, activity_upcoming_events.class);
        startActivity(i);
    }
}
