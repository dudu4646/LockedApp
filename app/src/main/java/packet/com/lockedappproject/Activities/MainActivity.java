package packet.com.lockedappproject.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;

public class MainActivity extends AppCompatActivity implements FireBase.UpdateUi {

    private static final String TAG = "WelcomeActivity";

    private ImageView p1, p2, p3, p4;
    private Button connect, signUp;
    private TextView userEmail, userPass;
    private EditText userInput, passInput;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout pb_layout;

    @Override
    protected void onStart() {
        super.onStart();
        FireBase.getNicks();
    }

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

