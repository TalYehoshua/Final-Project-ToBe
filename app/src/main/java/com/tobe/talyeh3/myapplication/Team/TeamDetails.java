package com.tobe.talyeh3.myapplication.Team;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.tobe.talyeh3.myapplication.AllUsersAdapter;
import com.tobe.talyeh3.myapplication.Chat.ChatActivity;
import com.tobe.talyeh3.myapplication.CreateGame.CreateGame;
import com.tobe.talyeh3.myapplication.CreateGame.TeamGamesActivity;
import com.tobe.talyeh3.myapplication.Gallery.GalleryActivity;
import com.tobe.talyeh3.myapplication.ProfilePage;
import com.tobe.talyeh3.myapplication.R;
import com.tobe.talyeh3.myapplication.Rating.RatingActivity;
import com.tobe.talyeh3.myapplication.Statistics.StatisticsActivity;
import com.tobe.talyeh3.myapplication.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class TeamDetails extends AppCompatActivity implements View.OnClickListener
{
    TextView btnLeave,tvName,btnAddPlayer,btnDelitePlayer,btnCreateGame,btnGames,btnStatistics,btnChat,btnGallery;
    TextView btnRating, btnPermissions;
    ImageView btnTeamPlayers;
    FirebaseDatabase database;
    DatabaseReference teamRef,userRef;
    ImageView user_profile_photo,btnMenu;
    Team t;
    String key;
    String photo="";
    String teamName;
    User u;
    private DatabaseReference databaseUser;
    Dialog d,menu;
    int firstPress=0;
    ListView lv;
    int i = 0;
    private DatabaseReference database2,teamDatabase;
    ArrayList<User> users;
    String keyUser="";
    AllUsersAdapter allPlayersAdapter;
    String myUserId;
    User user,user2;


    int per = 1;
    private DatabaseReference permissionDatabase;
    ArrayList<String> perInTeam;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_team_details);//try commit

        //FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
       // FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");

        getSupportActionBar().hide();
       // Toast.makeText(TeamDetails.this,"JJJJJJJJJJJJJJJJJJJJJ",Toast.LENGTH_SHORT).show();
        myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseUser = FirebaseDatabase.getInstance().getReference("Users");
        this.retriveData();
        //Toast.makeText(TeamDetails.this, "sd  "+firstPress,Toast.LENGTH_SHORT).show();
        user_profile_photo=(ImageView)findViewById( R.id.user_profile_photo);
        database = FirebaseDatabase.getInstance();
//        btnAddPlayer = (TextView) findViewById( R.id.btnAddPlayer);
//        btnDelitePlayer = (TextView) findViewById( R.id.btnDelitePlayer);
        tvName = (TextView) findViewById( R.id.tvName);
        btnMenu=(ImageView) findViewById( R.id.btnMenu );
        btnCreateGame=(TextView)findViewById( R.id.btnCreateGame );
        btnTeamPlayers=(ImageView) findViewById( R.id.btnTeamPlayers );
        btnStatistics=(TextView)findViewById( R.id.btnStatistics );
        btnChat=(TextView)findViewById( R.id.btnChat );
        btnGallery=(TextView)findViewById( R.id.btnGallery );
        btnGames=(TextView) findViewById( R.id.btnGames );
        btnRating=(TextView)findViewById(R.id.btnRating);
//        btnPermissions = (TextView)findViewById(R.id.btnPermissions);

        btnChat.setOnClickListener( this );
        btnGallery.setOnClickListener( this );
        btnStatistics.setOnClickListener( this );
        btnCreateGame.setOnClickListener( this );
        btnMenu.setOnClickListener( this );
        btnTeamPlayers.setOnClickListener( this );
//        btnAddPlayer.setOnClickListener( this );
//        btnDelitePlayer.setOnClickListener( this );
        btnRating.setOnClickListener( this );
        btnGames.setOnClickListener( this );
//        btnPermissions.setOnClickListener(this);

        Intent intent = getIntent();
        key = intent.getExtras().getString("keyteam");
        userRef = database.getReference("Users/" + myUserId);
        teamRef = database.getReference("Teams/" + key);
        this.retrieveData();
        //Toast.makeText(TeamDetails.this, "hghg         "+key, Toast.LENGTH_LONG).show();
        //for all players team
        database2 = FirebaseDatabase.getInstance().getReference("Teams/"+key+"/users");
        permissionDatabase = FirebaseDatabase.getInstance().getReference("Teams/" + key + "/permissions");

        permissionDatabase.addValueEventListener(new ValueEventListener()
        {

            public void onDataChange(DataSnapshot snapshot) {
                perInTeam = new ArrayList<String>();

                //////////////////////////////////////////////////////////////////////////
                // Permissions //
                for (DataSnapshot data : snapshot.getChildren()) {
                    String u = String.valueOf( data.getValue( ) );

                    //Toast.makeText( OpenTeam.this, "sd  " + u, Toast.LENGTH_SHORT ).show();
                    perInTeam.add( u );
                }


                myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (int i = 0; i < perInTeam.size(); i++)
                {
                    //Toast.makeText(StatisticsActivity.this, perInTeam.get(i), Toast.LENGTH_LONG).show();
                    if (perInTeam.get(i).equals(myUserId)) {//in the team but not me
                        per = 2;
                        break;
                    }
                }
/*
                if (per == 2) // if user have permmision he can see this buttons
                {
                    btnAddPlayer.setVisibility( View.VISIBLE);
                    btnDelitePlayer.setVisibility( View.VISIBLE);
                    btnPermissions.setVisibility(View.VISIBLE);
                }
*/
                /////////////////////////////////////////////////////////////////////////
            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void teamPlayers()
    {

        if(firstPress==0)
        {
            d= new Dialog(this);
            d.setContentView( R.layout.activity_all_users);

            d.setCancelable(true);
            lv = (ListView) d.findViewById( R.id.lv);
            this.retriveDataPlayers();
            d.show();
            //Toast.makeText(TeamDetails.this, "a"+firstPress,Toast.LENGTH_SHORT).show();
        }

        else
            {
            d.show();
                //Toast.makeText(TeamDetails.this, "b",Toast.LENGTH_SHORT).show();
        }
        d.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    d.cancel();
                    d.dismiss();
                    firstPress=1;
                }
                return true;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User u = users.get(position);
                Intent intent = new Intent(TeamDetails.this, ProfilePage.class);
                intent.putExtra("key", u.uid );
                intent.putExtra("photo", u.imgUrl );
                startActivity(intent);


            }





        });
    }


    public void retrieveData()
    {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(User.class);

               // Toast.makeText(TeamDetails.this,String.valueOf(u.teams.size()),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        teamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                t = dataSnapshot.getValue(Team.class);
                teamName=t.name;
                tvName.setText(t.name);
                photo= t.imgUrl;
                        Picasso
                        .with( TeamDetails.this )
                        .load( photo)
                        .fit() // will explain later
                        .into(user_profile_photo );


/*
                if (t.manager.equals( myUserId ))
                {
                    btnAddPlayer.setVisibility( View.VISIBLE);
                    btnDelitePlayer.setVisibility( View.VISIBLE);
                    btnPermissions.setVisibility(View.VISIBLE);
                }
*/
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void retriveDataPlayers() {
        database2.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                users = new ArrayList<User>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    keyUser = (String) snapshot.child(String.valueOf(i)).getValue();//put in array of users at the teams key and not mail

                    if (keyUser == null)
                    {
                        return;
                    }

                    //Toast.makeText(TeamDetails.this, "sd  "+firstPress,Toast.LENGTH_SHORT).show();
                    teamDatabase = FirebaseDatabase.getInstance().getReference("Users/" + keyUser);
                    ValueEventListener valueEventListener = teamDatabase.addValueEventListener(new ValueEventListener() {
                        public void onDataChange(DataSnapshot snapshot) {
                            User u = snapshot.getValue(User.class);
                            if (keyUser == null)
                            {
                                return;
                            }
                            users.add(u);
                            snapshot.toString();
                            allPlayersAdapter.notifyDataSetChanged();
                        }

                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                    allPlayersAdapter = new AllUsersAdapter(TeamDetails.this, 0, 0, users);
                    lv.setAdapter(allPlayersAdapter);

                    i++;

                }

            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void onClick(View v) {
        if (v==btnAddPlayer )
        {
            startActivity(getIntent());
            Intent intent = new Intent( TeamDetails.this, OpenTeam.class );
            intent.putExtra( "teamKey", t.key );
            finish();
            startActivity( intent );
        }
        if (v==btnLeave)
        {
            t.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (t.manager.equals( myUserId ) )
            {
                if (t.users.size()>1)
                    t.manager=t.users.get( 1 ) ;
                else
                    t.manager= "-1";
            }

            if (u.teams.size()==2)
            {
               // Toast.makeText(TeamDetails.this, u.teams.size() +"   llllllllllllllllllllllllllllllllllll  ",Toast.LENGTH_SHORT).show();
                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Users").child( myUserId ).child("teams").child( "0" ).setValue("-1");
                mDatabase.child("Users").child( myUserId ).child("teams").child( "1" ).removeValue();
                //userRef.setValue( u );
            }
            else
            {
                u.teams.remove(t.key);
                userRef.setValue( u );
            }
                t.users.remove( u.uid );
                t.statistics.remove(myUserId+t.key);
                String keyStatistics=myUserId+t.key;
                DatabaseReference statisticsPlayer = FirebaseDatabase.getInstance().getReference().getRoot().child("Statistics/"+keyStatistics);//remove Statistics player from team
                statisticsPlayer.setValue(null);
                teamRef.setValue( t );
                finish();

        }
        if (v==btnDelitePlayer )
        {
            startActivity(getIntent());
            Intent intent = new Intent( TeamDetails.this, OpenTeam.class );
            intent.putExtra( "teamKey", t.key );
            intent.putExtra( "delete","del" );
            finish();
            startActivity( intent );
        }
        if(v==btnTeamPlayers)
        {
            teamPlayers();
        }
        if (v==btnCreateGame)
        {

            Intent intent = new Intent( TeamDetails.this, CreateGame.class );
            intent.putExtra( "teamKey", key );
            intent.putExtra( "teamName", teamName );
            startActivity( intent );
        }
        if (v==btnStatistics)
        {
            Intent intent = new Intent( TeamDetails.this, StatisticsActivity.class );
            intent.putExtra( "teamKey", key );
            startActivity( intent );
        }
        if (v==btnGames)
        {
            Intent intent = new Intent( TeamDetails.this, TeamGamesActivity.class );
            intent.putExtra( "teamKey", key );
            intent.putExtra( "k", t.howMouchPlayers );
            //Toast.makeText(TeamDetails.this,  t.howMouchPlayers,Toast.LENGTH_SHORT).show();
            startActivity( intent );
        }
        if (v==btnChat)
        {
            Intent intent = new Intent( TeamDetails.this, ChatActivity.class );
            intent.putExtra( "teamKey", key );
            intent.putExtra( "teamName", teamName );
            intent.putExtra( "profilePic", user2.imgUrl );
            intent.putExtra( "userName", user2.userName );
            startActivity( intent );
        }
        if (v==btnGallery)
        {
            Intent intent = new Intent( TeamDetails.this, GalleryActivity.class );
            intent.putExtra( "teamKey", key );
            intent.putExtra( "teamName", teamName );
            intent.putExtra( "profilePic", user2.imgUrl );
            intent.putExtra( "userName", user2.userName );
            startActivity( intent );
        }
        if(v == btnRating)
        {
            Intent intent = new Intent( TeamDetails.this, RatingActivity.class );
            intent.putExtra( "teamKey", key );
            startActivity( intent );
        }
        if (v==btnMenu)
        {
            menu();
        }

        if(v == btnPermissions)
        {
            startActivity(getIntent());
            Intent intent = new Intent( TeamDetails.this, OpenTeam.class );
            intent.putExtra( "teamKey", t.key );
            intent.putExtra( "permissions","per" );
            finish();
            startActivity( intent );
        }

    }


    public void retriveData()
    {
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

    public void menu()
    {
        menu= new Dialog(this);
        menu.setContentView(R.layout.menuteamlayout);
        menu.setCancelable(true);

        btnLeave = (TextView) menu.findViewById( R.id.btnLeave);
        btnAddPlayer = (TextView) menu.findViewById( R.id.btnAddPlayer);
        btnDelitePlayer = (TextView) menu.findViewById( R.id.btnDelitePlayer);
        btnPermissions = (TextView) menu.findViewById( R.id.btnPermissions);

        btnLeave.setOnClickListener( this );

        if (per == 2) // if user have permission
        {

            btnAddPlayer.setOnClickListener( this );
            btnDelitePlayer.setOnClickListener( this );
            btnPermissions.setOnClickListener( this );
        }
        else // if user don't have permission
        {
            btnAddPlayer.setVisibility( View.INVISIBLE);
            btnDelitePlayer.setVisibility( View.INVISIBLE);
            btnPermissions.setVisibility(View.INVISIBLE);
        }
        menu.show();
    }
}


