package packet.com.lockedappproject.Activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;

public class MainActivity extends AppCompatActivity implements FireBase.UpdateUi {

    private static final String TAG = "WelcomeActivity";

    private ImageView p1, p2, p3, p4;
    private Button connect, signUp;
    private TextView userEmail, userPass;
    private EditText userInput, passInput;
    private SharedPreferences sharedPreferences;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ConstraintLayout pb_layout;

    @Override
    protected void onStart() {
        super.onStart();
        FireBase.getNicks();
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        connect = findViewById(R.id.connect);
        signUp = findViewById(R.id.signUp);
        userInput = findViewById(R.id.userInput);
        passInput = findViewById(R.id.passInput);
        userEmail = findViewById(R.id.userEmail);
        userPass = findViewById(R.id.userPass);

        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        p3 = findViewById(R.id.p3);
        p4 = findViewById(R.id.p4);

        //requesting and handling GPS permissions
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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
        };

        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        //getting the measurements of the screen for the animation
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;

        p1.setTranslationY((float) height);
        p2.setTranslationX((float) -height);
        p3.setTranslationX((float) height);
        p4.setTranslationY((float) -height);

        p1.animate().translationYBy((float) -height).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                activateLogin();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).setDuration(3000);
        p2.animate().translationXBy((float) height).setDuration(3000);
        p3.animate().translationXBy((float) -height).setDuration(3000);
        p4.animate().translationYBy((float) height).setDuration(3000);

        pb_layout = findViewById(R.id.pb_layout);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_layout.setVisibility(View.VISIBLE);
                String user = FireBase.getEmailFromNick(userInput.getText().toString().toLowerCase().trim());
                user = (user == null) ? userInput.getText().toString().toLowerCase().trim() : user;
                FireBase.signUp(user, passInput.getText().toString(), MainActivity.this);
            }
        });
    }


    //setting the connection option visible
    private void activateLogin() {
        userPass.setVisibility(View.VISIBLE);
        userEmail.setVisibility(View.VISIBLE);
        userInput.setVisibility(View.VISIBLE);
        passInput.setVisibility(View.VISIBLE);
        connect.setVisibility(View.VISIBLE);
        signUp.setVisibility(View.VISIBLE);

        //setting the last name if exists
        userInput.setText(sharedPreferences.getString("userEmail", ""));

        userPass.animate().alphaBy(1f).setDuration(1000);
        userEmail.animate().alphaBy(1f).setDuration(1000);
        passInput.animate().alphaBy(1f).setDuration(1000);
        userInput.animate().alphaBy(1f).setDuration(1000);
        connect.animate().alphaBy(1f).setDuration(1000);
        signUp.animate().alphaBy(1f).setDuration(1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void Success() {
        pb_layout.setVisibility(View.GONE);
        sharedPreferences.edit().putString("userEmail", userInput.getText().toString()).apply();
        startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
    }

    @Override
    public void Failed(Exception e) {
        Log.w(TAG, "signInWithEmail:failure", e.getCause());
        pb_layout.setVisibility(View.INVISIBLE);
        Toast.makeText(MainActivity.this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }
}

