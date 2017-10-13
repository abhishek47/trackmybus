package com.trumpets.trackmybus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements LocationListener {

    SharedPreferences sharedPreferences;
    public static final String APPPREFERENCES = "AppPrefs";
    public static final String TRACKER_KEY = "trackerKey";

    private TextView currentLocation;
    private TextView trackerId;

    DatabaseReference busesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sharedPreferences = getSharedPreferences(APPPREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.getString(TRACKER_KEY, null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TRACKER_KEY, UUID.randomUUID().toString().substring(0, 7));
            editor.commit();
        }

        currentLocation = (TextView) findViewById(R.id.currentLocation);
        trackerId = (TextView) findViewById(R.id.trackerId);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        busesRef = database.getReference("buses/" + sharedPreferences.getString(TRACKER_KEY, "1234"));


        trackerId.setText(sharedPreferences.getString(TRACKER_KEY, "Not Configured"));




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addBusIntent = new Intent(MainActivity.this, AddBusActivity.class);
                startActivity(addBusIntent);


            }
        });
    }

    private void syncLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = locationManager.getLastKnownLocation(bestProvider);
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        HashMap<String, Double> loc = new HashMap<String, Double>();

        loc.put("lat", latitude);
        loc.put("lng", longitude);


        DatabaseReference locations = busesRef.push();
        locations.setValue(loc);

    }

    @Override
    protected void onResume() {
        super.onResume();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    @Override
    public void onLocationChanged(Location location) {
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        HashMap<String, Double> loc = new HashMap<String, Double>();

        loc.put("lat", latitude);
        loc.put("lng", longitude);


        DatabaseReference locations = busesRef.push();
        locations.setValue(loc);

        currentLocation.setText(String.valueOf(round(latitude, 6)) + ", " + String.valueOf(round(longitude, 6)));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
