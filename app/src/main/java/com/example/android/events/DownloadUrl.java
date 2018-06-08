package com.example.android.events;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadUrl {

    public String readUrl(String myUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            //Creating a url
            URL url = new URL(myUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Read data from url
            inputStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer sb = new StringBuffer();

            //Reading each line and appending to string buffer
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            //Convert stringbuffer to string
            data = sb.toString();
            //Log.d("downloadUrl", data.toString());
            br.close();
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
