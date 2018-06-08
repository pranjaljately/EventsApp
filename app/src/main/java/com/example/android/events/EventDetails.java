package com.example.android.events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EventDetails extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Getting Event Address from EventsLocation Activity and displaying it
        Intent myIntent = getIntent();
        if (myIntent.hasExtra("EventAddress")){
            TextView selectedAddress = (TextView)findViewById(R.id.Event_Address);
            selectedAddress.setText(myIntent.getStringExtra("EventAddress")); }

        //Getting Item Address from Item Location Activity and displaying it
        Intent itemIntent = getIntent();
        if (itemIntent.hasExtra("ItemAddress")){
            TextView selectedItemAddress = (TextView)findViewById(R.id.Item_Address);
            selectedItemAddress.setText(myIntent.getStringExtra("ItemAddress")); }
    }

    //Used to open event location (place picker activity)
    public void PassMapID(View v)
    {
        Intent i = new Intent(this, ItemLocation.class);
        startActivity(i);
    }

}
