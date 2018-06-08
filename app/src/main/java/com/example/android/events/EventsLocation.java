package com.example.android.events;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class EventsLocation extends AppCompatActivity {
    int PLACE_PICKER_REQUEST = 1;
    private String address;
    private GeoDataClient mGeoDataClient;
    private GoogleApiClient mGoogleApiClient;
    public String placeId = "849VCWC9+92Q7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_events_location);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
//        if (placeId != null) {
//        mGeoDataClient.getPlaceById().addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                if (task.isSuccessful()) {
//                    PlaceBufferResponse places = task.getResult();
//                    Place myPlace = places.get(0);
//                    Log.i("Test", "Place found: " + myPlace.getName());
//                    places.release();
//                } else {
//                    Log.e("Test", "Place not found.");
//                }
//            }
//        });

////            Places.GeoDataApi.getPlaceById(mGoogleApiClient,
////                    "849VCWC9+92Q7");
//        }
    }
    //When user has selected place using place picker
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String location = place.getAddress().toString();//turn the address of the place selected to string
                String toastMsg = "Location:" + location;
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();//displaying a message on the screen
                Intent i = new Intent(this, EventDetails.class);//creating an intent
                i.putExtra("EventAddress", location);//used to pass address to other activity (event details)
                startActivity(i);
            }
        }
    }





}

