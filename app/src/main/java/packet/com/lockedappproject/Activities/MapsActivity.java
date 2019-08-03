package packet.com.lockedappproject.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentLocation;
    private float zoom = 0;
    private LocationManager locationManager;
    private Float lat, lot;
//    private String name = null;

    private House house;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String id = getIntent().getStringExtra("houseId");
        house = FireBase.getOneHouse(id);
        if (house.lat!=0 && house.lot!=0 ) {
            currentLocation = new LatLng(house.lat, house.lot);
        } else {
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new CountDownTimer(5000, 275) {
            @Override
            public void onTick(long millisUntilFinished) {
                mMap.addMarker(new MarkerOptions().position(currentLocation).title(house.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom));
                zoom++;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), HouseScreen.class);
                intent.putExtra("houseId", house.id);
                startActivity(intent);
                MapsActivity.this.finish();
            }
        }.start();
    }
}
