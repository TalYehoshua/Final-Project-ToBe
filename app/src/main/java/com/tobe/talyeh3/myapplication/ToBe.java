package com.tobe.talyeh3.myapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tobe.talyeh3.myapplication.Chat.ChatActivity;
import com.tobe.talyeh3.myapplication.Posts.AllPostActivity;
import com.tobe.talyeh3.myapplication.Team.MyTeams;
import com.tobe.talyeh3.myapplication.Team.OpenTeamDetails;
import com.tobe.talyeh3.myapplication.Weather.WeatherActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ToBe extends AppCompatActivity implements View.OnClickListener
     {
    FirebaseAuth firebaseAuth;
    TextView btnAllUsers, btnOpenTeam, btnMyTeams, btnChat, btnAddFriends, logOut,MyProfile, btnAllPost, btnWeather;
    Button btnMenu;
    Dialog d;
    ProgressDialog progressDialog;
    FirebaseUser us;
    String myUserId;
    private DatabaseReference databaseUser;
    User user,user2;
    private static final int REQUEST_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_to_be_test );

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();

        if(firebaseUser!=null) // Main page
        {
            us= FirebaseAuth.getInstance().getCurrentUser();
            myUserId = us.getUid();
        }
        else//registeration page
        {
            Intent intent = new Intent(ToBe.this, RegisterActivity.class);
            startActivity(intent);
        }

        databaseUser = FirebaseDatabase.getInstance().getReference("Users");

        // get permissions to user location
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        // initializations //
        btnAllPost = (TextView)findViewById(R.id.btnAllPost);
        btnWeather = (TextView)findViewById(R.id.btnWeather);
        btnMyTeams = (TextView)findViewById(R.id.btnMyTeams);

        retriveData();
        progressDialog = new ProgressDialog(this);

        btnMenu = (Button)findViewById( R.id.btnMenu );
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu();

            }
        });

        btnOpenTeam = (TextView)findViewById(R.id.btnOpenTeam);
        btnOpenTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user2== null)
                {
                    Toast.makeText(ToBe.this, "There is no internet connection, please try again.. ", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(ToBe.this,OpenTeamDetails.class);
                startActivity(intent);

            }
        });

        btnChat = (TextView)findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user2== null)
                {
                    Toast.makeText(ToBe.this, "There is no internet connection, please try again.. ", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(ToBe.this,ChatActivity.class);
                intent.putExtra( "profilePic", user2.imgUrl );
                intent.putExtra( "userName", user2.userName );
                startActivity(intent);

            }
        });

        btnAllPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user2== null)
                {
                    Toast.makeText(ToBe.this, "There is no internet connection, please try again.. ", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(ToBe.this,AllPostActivity.class);
                startActivity(intent);
            }
        });

        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user2 == null)
                {
                    Toast.makeText(ToBe.this, "There is no internet connection, please try again.. ", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(ToBe.this,WeatherActivity.class);
                startActivity(intent);
            }
        });



        btnMyTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToBe.this,MyTeams.class);
                startActivity(intent);
            }
        });



        btnAllUsers = (TextView)findViewById(R.id.btnAllUsers);
        btnAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToBe.this,AllUsers.class);
                startActivity(intent);

            }
        });

        btnAddFriends = (TextView)findViewById(R.id.btnAddFriends);
        btnAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user2== null)
                {
                    Toast.makeText(ToBe.this, "There is no internet connection, please try again.. ", Toast.LENGTH_LONG).show();
                    return;
                }

                //Intent to share link with friends to download the app //
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "בוא להיות חלק מהמשחק. הורד את האפליקציה ToBe עוד היום ותתחבר לשחקנים האמיתיים https://play.google.com/store/apps/details?id=com.tobe.talyeh3.myapplication");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.project_id)));
            }
        });

    }



         public void menu()
         {
             d= new Dialog(this);
             d.setContentView(R.layout.menulayout);
             d.setCancelable(true);

             logOut = (TextView) d.findViewById(R.id.logOut);
             MyProfile = (TextView) d.findViewById(R.id.MyProfile);
             logOut.setOnClickListener(this);
             MyProfile.setOnClickListener(this);
             d.show();

         }


         @Override
         public void onClick(View view) {

             if (view==btnMenu)
             {
                 menu();
             }
             if(view==logOut)
             {
                 firebaseAuth.signOut();
                 Intent intent = new Intent(ToBe.this,RegisterActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
             }
             if(view==MyProfile)
             {
                 Intent intent = new Intent(ToBe.this, ProfilePage.class);
                 intent.putExtra("key", myUserId);
                 d.dismiss();
                 startActivity(intent);
             }
         }

         public void retriveData() {
             databaseUser.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     for (DataSnapshot data : dataSnapshot.getChildren()) {
                         user = data.getValue(User.class);
                         if (user.uid.equals( myUserId ))
                         {
                             user2 = user;
                         }
                     }
                 }
                 @Override
                 public void onCancelled(DatabaseError databaseError) {
                 }
             });
         }
     }