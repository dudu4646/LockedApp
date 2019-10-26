package packet.com.lockedappproject.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.User;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";

    private EditText nick, email, pass, repass;
    private Button signUp, goBack;
    private boolean nickF, emailF, passF, reF;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        //set elements
        nick = findViewById(R.id.nick);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);
        signUp = findViewById(R.id.signUp);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        nickF = emailF = passF = reF = true;
        nick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if ((nick.getText().toString().length() <= 0) || (nick.getText().toString().trim().equals(""))) {
                        nick.setError("This field cannot be blank");
                        nickF = true;
                    } else {
                        if (FireBase.checkNick(nick.getText().toString())) {
                            nick.setError("This nick already taken");
                            nickF = true;
                        } else {
                            nick.setError(null);
                            nickF = false;
                        }
                    }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        email.setError("Email address isn't valid");
                        emailF = true;
                    } else {
                        email.setError(null);
                        emailF = false;
                    }
            }
        });
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if ((pass.getText().toString().length() < 6) ||
                            pass.getText().toString().trim().equalsIgnoreCase("")) {
                        pass.setError("your password must be at least 6 characters long");
                        passF = true;
                    } else {
                        pass.setError(null);
                        passF = false;
                    }
            }
        });

        repass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((s.toString().length() != pass.getText().toString().length()) &&
                        (!s.toString().equals(pass.getText().toString()))) {
                    repass.setError("password isn't match");
                    reF = true;
                } else {
                    repass.setError(null);
                    reF = false;
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nickF || emailF || passF || reF) {
                    Toast.makeText(Register.this, "Check the data", Toast.LENGTH_SHORT).show();
                } else {
                    if (FireBase.checkNick(nick.getText().toString()))
                        Toast.makeText(Register.this, "User name already taken", Toast.LENGTH_SHORT).show();
                    else {
                        auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            addUser();
                                            Toast.makeText(Register.this, "User create!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(Register.this, "Email address already taken",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void addUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(user.getUid()).setValue(new User("", "", FirebaseAuth.getInstance().getCurrentUser().getEmail(), nick.getText().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                close();
            }
        });
    }

    private void close(){
        this.finish();
    }
}


