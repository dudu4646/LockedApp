package packet.com.lockedappproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;
import packet.com.lockedappproject.models.Lock;
import packet.com.lockedappproject.models.User;

public class AddLock extends AppCompatActivity implements FireBase.FindLock {

    private EditText lockId;
    private Button search;
    private String toHouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lock);

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
                search.setEnabled(editable.toString().trim().length() > 0);
            }
        });
        //Buttons
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = lockId.getText().toString();
                User user = FireBase.getUser();
                if (!user.lockList.equalsIgnoreCase("") && user.lockList.contains(str)) {
                    Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
                    House house = FireBase.getHousebyLock(str);
                    Lock lock = FireBase.getLockByStr(str);
                    intent.putExtra("status", 1);
                    intent.putExtra("houseName", house.name);
                    intent.putExtra("lockName", lock.name);
                    startActivity(intent);
                    finish();

                } else
                    FireBase.searchGeneralLock(str, AddLock.this);
            }
        });

        toHouse = getIntent().getStringExtra("houseName");
    }

    @Override
    public void found(House house, Lock lock) {
        Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
        intent.putExtra("status", 2);
        intent.putExtra("houseName", house.name);
        intent.putExtra("lockName", lock.name);
        intent.putExtra("lockId", lock.id);
        intent.putExtra("lockAdmins", lock.admin);
        startActivity(intent);
        finish();
    }

    @Override
    public void notFound(String lId) {
        Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
        intent.putExtra("status", 3);
        intent.putExtra("houseName",toHouse);
        intent.putExtra("houseId",lockId.getText().toString().trim());
        startActivity(intent);
        finish();
    }
}
