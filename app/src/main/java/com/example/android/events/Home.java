package com.example.android.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telecom.Call;
import android.view.View;

public class Home extends AppCompatActivity implements View.OnClickListener{
    private CardView birthdayCard,meetingCard,sportsCard,homeCard,addCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        birthdayCard = (CardView) findViewById(R.id.birthday_card);
        meetingCard = (CardView) findViewById(R.id.meeting_card);
        sportsCard = (CardView) findViewById(R.id.sports_card);
        homeCard = (CardView) findViewById(R.id.home_card);
        addCard = (CardView) findViewById(R.id.add_card);

        birthdayCard.setOnClickListener(this);
        meetingCard.setOnClickListener(this);
        sportsCard.setOnClickListener(this);
        homeCard.setOnClickListener(this);
        addCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i ;

        switch(v.getId()) {
            case R.id.birthday_card: i = new Intent(this, details.class);startActivity(i);break;
            case R.id.meeting_card: i = new Intent(this, details.class);startActivity(i);break;
            case R.id.sports_card: i = new Intent(this, details.class);startActivity(i);break;
            case R.id.home_card: i = new Intent(this, details.class);startActivity(i);break;
            case R.id.add_card: i = new Intent(this, details.class);startActivity(i);break;
            default:break;

        }

    }
}
