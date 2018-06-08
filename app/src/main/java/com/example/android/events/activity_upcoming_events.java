package com.example.android.events;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class activity_upcoming_events extends AppCompatActivity {

    private static final String TAG = "UpcomingEvents";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new UpcomingEvents(), "Upcoming Events");
        adapter.addFragment(new PastEvents(), "Past Events");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_upcoming_events, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       switch ( item.getItemId()){
           case R.id.action_add:
               Intent homeintent = new Intent(this, Home.class);
            //Start Home Activity
            startActivity(homeintent);
           break;
           case R.id.log_out:
               sp=getSharedPreferences("login",MODE_PRIVATE);
               SharedPreferences.Editor e=sp.edit();
               e.clear();
               e.commit();
               finish();
               Intent login = new Intent(this, LogIn.class);
               //Start login Activity
               startActivity(login);
           break;

       }
        return true;
////        //When Product action item is clicked
////        if (id == R.id.action_add) {
////            //Create Intent for Home Activity
////            Intent homeintent = new Intent(this, Home.class);
////            //Start Home Activity
////            startActivity(homeintent);
////            return true;
////        }
//        return super.onOptionsItemSelected(item);
        }


}
