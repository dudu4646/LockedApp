package packet.com.lockedappproject.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import packet.com.lockedappproject.R;

public class Test_AddLock extends AppCompatActivity {

    private EditText lockId;
    private Button serch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__add_lock);

        //EditText
        lockId = findViewById(R.id.lockId);
        lockId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                serch.setEnabled(editable.toString().trim().length()>0);
            }
        });
        //Buttons
        serch=findViewById(R.id.serch);


    }
}
