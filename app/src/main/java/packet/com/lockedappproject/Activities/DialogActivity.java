package packet.com.lockedappproject.Activities;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.Lock;

public class DialogActivity extends AppCompatActivity {

    private Button ok,cancel;
    private ConstraintLayout takenLayout,foundLayout,newLockLayout,dltLockLayout;
    private TextView header;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);

        //Buttons
        ok=findViewById(R.id.ok);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //ConstraintLayout
        takenLayout = findViewById(R.id.takenLayout);
        foundLayout = findViewById(R.id.foundLayout);
        newLockLayout = findViewById(R.id.newLockLayout);
        dltLockLayout = findViewById(R.id.dltLockLayout);
        //TextView
        header = findViewById(R.id.header);
        //ImageView
        img = findViewById(R.id.img);

        int status = getIntent().getIntExtra("status",0);

        switch (status){
            //user already signed to lock
            case 1:
                header.setText("Lock already exists");
                takenLayout.setVisibility(View.VISIBLE);
                TextView msg1 = findViewById(R.id.msg1);
                msg1.setText(R.string.lockexistsP1);
                TextView houseName = findViewById(R.id.houseName);
                houseName.setText(getIntent().getStringExtra("houseName"));
                houseName.setTypeface(Typeface.DEFAULT_BOLD);
                TextView msg2 = findViewById(R.id.msg2);
                msg2.setText(R.string.lockexistsP2);
                TextView lockName = findViewById(R.id.newLock);
                lockName.setTypeface(Typeface.DEFAULT_BOLD);
                lockName.setText(getIntent().getStringExtra("lockName"));
                cancel.setVisibility(View.GONE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                break;
            //new lock and new house:
            case 2:
                header.setText("Add lock");
                img.setImageResource(R.drawable.add);
                foundLayout.setVisibility(View.VISIBLE);
                TextView found2 = findViewById(R.id.found2);
                found2.setText(getIntent().getStringExtra("houseName"));
                found2.setTypeface(Typeface.DEFAULT_BOLD);
                TextView found4 = findViewById(R.id.found4);
                found4.setText(getIntent().getStringExtra("lockName"));
                found4.setTypeface(Typeface.DEFAULT_BOLD);

                final String lockAdmins = getIntent().getStringExtra("lockAdmins");
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(DialogActivity.this, "שליחת בקשה לאדמינים של המנעול", Toast.LENGTH_SHORT).show();
                        System.out.println("testing --> "+lockAdmins);
                        finish();
                    }
                });
                break;
            //new lock from house
            case 3:
                header.setText("Add lock");
                img.setImageResource(R.drawable.add);
                newLockLayout.setVisibility(View.VISIBLE);
                break;
            //delete house
            case 4:
                header.setText("Delete lock");
                img.setImageResource(R.drawable.dlt);
                dltLockLayout.setVisibility(View.VISIBLE);
                final String lName = getIntent().getStringExtra("lockName");
                TextView dltLockName = findViewById(R.id.dltLockName);
                dltLockName.setText(lName);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FireBase.deleteLock(lName);
                        finish();
                    }
                });
                break;
        }
    }

}
