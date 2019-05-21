package cytex.co.zw.concertfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cytex.co.zw.concertfinder.fragments.AddConcert;
import cytex.co.zw.concertfinder.fragments.AddConcertOnLocation;
import cytex.co.zw.concertfinder.fragments.MyConcerts;
import cytex.co.zw.concertfinder.fragments.ReportFragment;
import cytex.co.zw.concertfinder.fragments.ViewConcerts;
import cytex.co.zw.concertfinder.models.Concert;
import cytex.co.zw.concertfinder.models.Like;
import cytex.co.zw.concertfinder.models.Report;
import cytex.co.zw.concertfinder.utils.Constants;

public class NavMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    // Creating DatabaseReference.
    DatabaseReference databaseReference,likesDatabaseReference;
    List<Concert> listConcerts;
    List<Like> listLikes;
    List<Report> listReports;

    private final static String TAG="NavMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listConcerts=new ArrayList<>();
        listLikes=new ArrayList<>();
        listReports=new ArrayList<>();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        ViewConcerts viewConcerts=new ViewConcerts();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame,viewConcerts,"View Concerts");
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.Database_Path);
        likesDatabaseReference=FirebaseDatabase.getInstance().getReference(Constants.TABLE_LIKES);

        // Adding Add Value Event Listener to databaseReference.
        likesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Like like = postSnapshot.getValue(Like.class);
                    listLikes.add(like);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.


            }
        });




        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Concert concert = postSnapshot.getValue(Concert.class);

                    listConcerts.add(concert);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_main, menu);
        return true;
    }

    public void showMassage(String Title,String Message)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(Title);
        builder.setMessage(Message);
        builder.show();
    }

    private void aboutApp(){
        showMassage("About this App","\n\n \t This app help the concert organisers by \n" +
                "1.\t Publish their concerts\n" +
                "2.\t Show concerts with location \n" +
                "3.\t Enable attenders to chat\n" +
                ".\t All rights reserved\n"+
                "-------------------------\n\n"+
                "\t Developed by Anywherem17 2019\n\n\n");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            aboutApp();
            return true;
        }
        else if(id==R.id.action_logout){
            android.app.AlertDialog.Builder alertDialogBuilder =  new android.app.AlertDialog.Builder(this)
                    .setTitle("Logout?")
                    .setMessage("Are you sure you want to Logout from " + getString(R.string.app_name) + "?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            signOut();
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert);

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        for(Concert concert:listConcerts){
            if(concert.getDescription().length()>8){
              concert.setDescription(concert.getDescription().substring(0,8)+"...");
            }
            int j=0;
            for(Like like:listLikes){
                if(like.getConcertId().equals(concert.getId())){
                    j++;
                }
            }
            listReports.add(new Report(concert.getDescription(),j));
        }

        if (id == R.id.view_concerts) {
            ViewConcerts viewConcerts=new ViewConcerts();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame,viewConcerts,"View Concerts");
            transaction.commit();
            // Handle the camera action
        } else if (id == R.id.add_concert) {
            AddConcert addConcert=new AddConcert();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame,addConcert,"Add Concert");
            transaction.commit();

        }

        else if(id==R.id.add_concert_on_location){
            AddConcertOnLocation addConcert=new AddConcertOnLocation();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame,addConcert,"Add Concert On Location");
            transaction.commit();
        }


        else if (id == R.id.view_by_location) {
            startActivity(new Intent(NavMain.this,NearbyParties.class));

        } else if (id == R.id.my_parties) {
            MyConcerts myConcerts=new MyConcerts();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame,myConcerts,"My Concerts");
            transaction.commit();

        } else if (id == R.id.about_app) {
            aboutApp();
        }

        else if (id == R.id.report) {
            //startActivity(new Intent(getApplicationContext(),ViewReport.class));
            Log.d(TAG,listConcerts.toString());
            Log.d(TAG,listLikes.toString());
            Log.d(TAG,listReports.toString());
            ReportFragment reportFragment=new ReportFragment(listReports);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame,reportFragment,"Report Fragment");
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //sign out method
    public void signOut() {
        auth.signOut();
        startActivity(new Intent(NavMain.this,MainActivity.class));
    }
}
