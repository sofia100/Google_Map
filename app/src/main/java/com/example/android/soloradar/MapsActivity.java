package com.example.android.soloradar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
   // FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef ;
    LocationUnit loc;
    GoogleMap gmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Solo Radar");
        Log.v("db first", "here");
        loc=new LocationUnit();

        Log.v("db first", "her2e");
        myRef = FirebaseDatabase.getInstance().getReference("locations");

        Log.v("db first", "here3");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Log.v("db first", "here4");
        fetchLocation();

        Log.v("db first", "here5");
       // mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        Log.v("db first", "here6");
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                Log.v("db first", "here7");
                if (location != null) {
                    currentLocation = location;
                    loc.setLatitue(currentLocation.getLatitude());
                    loc.setLongitude(currentLocation.getLongitude());

                    Log.v("db first", "here8");
                    Toast.makeText(getApplicationContext(), "Latitude: "+currentLocation.getLatitude() + "\nLongitude" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                    myRef.push().setValue(loc);
                    Toast.makeText(getApplicationContext(),"uploaded to db", Toast.LENGTH_SHORT).show();
                    Log.v("db first", "here9");


                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    Log.v("db first", "here81");

                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
       // myMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        Log.v("db first", "here82");


        gmap=googleMap;

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               // String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "Value is: " + value);
                Log.v("db first", "here83");

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    LocationUnit val=postSnapshot.getValue(LocationUnit.class);
                    Log.v("db first", "here84");

                    LatLng latLng = new LatLng(val.getLatitue(), val.getLongitude());
                    Log.v("db first", "here85");

                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
                    Log.v("db first", "here86");

                    gmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                    gmap.addMarker(markerOptions);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(getApplicationContext(),"failed to read",Toast.LENGTH_SHORT).show();
            }
        });

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
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
   /* @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/
}
