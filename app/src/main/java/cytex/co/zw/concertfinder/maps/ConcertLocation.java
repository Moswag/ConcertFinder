package cytex.co.zw.concertfinder.maps;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.utils.MessageToast;

public class ConcertLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat,lng;
    String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent= getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            lat = Double.valueOf(extras.getString("latitude"));
            lng = Double.valueOf(extras.getString("longitude"));
            description = extras.getString("description");
        }
        else{
            lat=0.0;
            lng=0.0;
            description="ma1";
        }


      //  MessageToast.show(getApplicationContext(),"Latlong: "+lat+" "+lng+" description"+description);


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title(description));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
