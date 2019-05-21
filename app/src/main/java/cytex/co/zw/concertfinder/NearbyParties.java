package cytex.co.zw.concertfinder;

import android.Manifest;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cytex.co.zw.concertfinder.adapter.ViewConcertsAdapter;
import cytex.co.zw.concertfinder.models.Concert;
import cytex.co.zw.concertfinder.utils.Constants;
import cytex.co.zw.concertfinder.utils.GPSTracker;
import cytex.co.zw.concertfinder.utils.MessageToast;

public class NearbyParties extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private  static final  int REQUEST_CODE_PERMISSION=2;
    String mPermission= Manifest.permission.ACCESS_FINE_LOCATION;

    GPSTracker gps;
    double lat,longi;
    List<Concert> concertList=new ArrayList<>();
    List<Concert> filtedConcerts=new ArrayList<>();

    // Creating Progress dialog
    ProgressDialog progressDialog;

    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_parties);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FirebaseApp.initializeApp(getApplicationContext());

        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.Database_Path);


        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    Concert concert = postSnapshot.getValue(Concert.class);

                    concertList.add(concert);
                }


                for(Concert concert:concertList){
                    if(concert.getHasMap()){
                        filtedConcerts.add(concert);
                    }
                }
                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });


        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getApplicationContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Concerts Locations.");

        // Showing progress dialog.
//        progressDialog.show();

        gps=new GPSTracker(getApplicationContext());
        if (gps.canGetLocation()){
            lat=gps.getLatitude();
            longi=gps.getLongitude();
        }
        else{
            gps.showSettingsAlert();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MessageToast.show(getApplicationContext(),"Before filter:"+concertList.size()+" After filter"+filtedConcerts.size());
        filtedConcerts.add(new Concert("Current Position",lat,longi));
        filtedConcerts.add(new Concert("Best Party",-19.43081385,29.76503338));


        // Add a marker in Sydney and move the camera
        for(Concert c:filtedConcerts){
            LatLng latLng = new LatLng(c.getLatitude(), c.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(c.getCategory()));
         //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}
