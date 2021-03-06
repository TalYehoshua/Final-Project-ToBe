package com.tobe.talyeh3.myapplication.CreateGame;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tobe.talyeh3.myapplication.Game;
import com.tobe.talyeh3.myapplication.R;
import com.tobe.talyeh3.myapplication.Team.Team;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CreateGame extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private static final String TAG = "CreateGame";
    private TextView mDisplayDate,mDisplayTime,btnLocation;
    private EditText etMinimumPlayers;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private final int REQUEST_CODE_PLACEPICKER = 1;

    Button btnCreateGame;
    public FirebaseDatabase database;
    public DatabaseReference teamRef, gameRef, teamRef2;
    String userOpen = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    public ArrayList<String> games;
    public Team t;
    ProgressDialog progressDialog;
    String keyTeam;
    String teamName;

    String myUserId;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mNotificationDatabase;
    ArrayList<String > usersInTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_create_game );
        getSupportActionBar().hide();
        etMinimumPlayers = (EditText) findViewById(R.id.etMinimumPlayers);
//********************************date and time********************************
        mDisplayDate = (TextView) findViewById(R.id.btnDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateGame.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };
        mDisplayTime = (TextView) findViewById(R.id.btnTime);
        mDisplayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);


                TimePickerDialog dialog = new TimePickerDialog(CreateGame.this,CreateGame.this,hour,minute,
                        android.text.format.DateFormat.is24HourFormat(CreateGame.this ));
                dialog.show();
            }
        });

        //********************************date and time********************************

        //********************************location********************************
        btnLocation = (TextView) findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlacePickerActivity();
            }
        });
        //********************************date and time********************************

        //********************************Firbase Push********************************
        Intent intent = getIntent();
        keyTeam = intent.getExtras().getString("teamKey");
        teamName = intent.getExtras().getString("teamName");
        //Toast.makeText(CreateGame.this, "hghg         "+keyTeam, Toast.LENGTH_LONG).show();
        database = FirebaseDatabase.getInstance();
        btnCreateGame = (Button) findViewById( R.id.btnCreateGame );
        btnCreateGame.setOnClickListener(this );
        teamRef = database.getReference( "Teams/" + keyTeam );
        getUsersInTeam();


        progressDialog = new ProgressDialog( this );

        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child( "notifications" );
        myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.retriveData();



    }


    public void getUsersInTeam()
    {
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child( "Teams/"+keyTeam+"/users/" );

        mUserDatabase.addValueEventListener(new ValueEventListener() {//users in team
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usersInTeam = new ArrayList<String>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String s = String.valueOf( data.getValue() );
                    usersInTeam.add(s);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Log.d(TAG, "onDateSet: mm/dd/yyy: " + hour + "/" + minute);

        String time = hour + ":" + minute;
        mDisplayTime.setText(time);
    }



    private void startPlacePickerActivity() {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        // this would only work if you have your Google Places API working

        Intent intent;
        try {
            intent = intentBuilder.build( this);
            startActivityForResult(intent, REQUEST_CODE_PLACEPICKER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displaySelectedPlaceFromPlacePicker(Intent data) {
      //  Place placeSelected = PlacePicker.getPlace(data, this);
        Place placeSelected = PlacePicker.getPlace(this, data);
        String name = placeSelected.getName().toString();
        String address = placeSelected.getAddress().toString();
        String address2 = placeSelected.getLatLng().toString();
        //Toast.makeText(CreateGame.this, address2, Toast.LENGTH_LONG).show();
        TextView enterCurrentLocation = (TextView) findViewById(R.id.btnLocation);
        enterCurrentLocation.setText(name + ", " + address);
    }


    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PLACEPICKER && resultCode == RESULT_OK) {
            displaySelectedPlaceFromPlacePicker(data);
        }
    }


    @Override
    public void onClick(View view) {
        if (view == btnCreateGame)
        {
            if(etMinimumPlayers.getText().length()<=0 || btnLocation.getText().toString().equals( "" )||mDisplayDate.getText().toString().equals( "" )||mDisplayTime.getText().toString().equals( "" ) )
            {
                Toast.makeText(CreateGame.this, "Some Fields Are Empty", Toast.LENGTH_LONG).show();
                return;
            }
            String uid = FirebaseAuth.getInstance().getCurrentUser().toString();
            List<String> whoIsComming;
            whoIsComming = new ArrayList<String>();
            whoIsComming.add("-1");
            Game g = new Game( mDisplayDate.getText().toString(), mDisplayTime.getText().toString(), btnLocation.getText().toString(),  Integer.valueOf(etMinimumPlayers.getText().toString()), "" ,keyTeam,userOpen,0,whoIsComming);
          gameRef = database.getReference( "Games" ).push();
            g.key = gameRef.getKey();


            teamRef = database.getReference( "Teams/" +keyTeam );
            teamRef2 = database.getReference( "Teams/" + keyTeam + "/games/0" );


            t.games.add( gameRef.getKey() );
            gameRef.setValue( g );
            teamRef.setValue( t );
            if (teamRef2 != null) {
                teamRef2.removeValue();
            }

            if (usersInTeam!=null)
            {
                for (int i = 0 ; i < usersInTeam.size(); i++) {
                      if (!myUserId.equals(  usersInTeam.get( i )))
                    {
                        HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put( "from", myUserId );
                        notificationData.put( "type", "Created a new game in Team "+teamName );
                        mNotificationDatabase.child( usersInTeam.get( i ) ).push().setValue( notificationData ).addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        } );
                    }
                }
            }




                finish();
        }


    }




    private void retriveData() {
        teamRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                t = dataSnapshot.getValue( Team.class );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );
    }




}
