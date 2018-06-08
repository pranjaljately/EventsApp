package com.example.android.events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting ");
        Button btNavToUpcoming = (Button) findViewById(R.id.GotoUpcomingEventsScreen);

        Button btNavtoList = (Button) findViewById(R.id.Gotolist);
        Button Gotodetails = (Button) findViewById(R.id.Gotodetails);

        Gotodetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked Gotodetails");
                Intent intent = new Intent(MainActivity.this, details.class);
                startActivity(intent);
            }

        });
        btNavToUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked btNavToUpcoming.");
                Intent intent = new Intent(MainActivity.this, activity_upcoming_events.class);
                startActivity(intent);

            }
        });


//List button listener and function to open list activity
        btNavtoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LIST.class);
                startActivity(i);
            }
        });






    }
    //Open event details page
    public void openEventDetails(View view){
        Intent i = new Intent(this, EventsLocation.class);
        startActivity(i);
    }

    public void tempLogin(View view){
        Intent i = new Intent(this, LogIn.class);
        startActivity(i);
    }

    public void tempHome(View view){
        Intent i = new Intent(this, Home.class);
        startActivity(i);
    }


}
