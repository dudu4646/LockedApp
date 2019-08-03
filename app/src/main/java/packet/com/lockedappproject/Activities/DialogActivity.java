package packet.com.lockedappproject.Activities;

import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.Lock;

public class DialogActivity extends AppCompatActivity {

    private static final String TAG = "DialogActivity";


    private TextView alert, header, msg;
    private Button cancel, ok;
    private ConstraintLayout lastUser_lock;
    private ImageView pic;
    private ListView userList;
    private ConstraintLayout last_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);

        ok = findViewById(R.id.ok);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        alert = findViewById(R.id.alert);
        ConstraintLayout.LayoutParams alertParam = (ConstraintLayout.LayoutParams) alert.getLayoutParams();
        pic = findViewById(R.id.pic);
        header = findViewById(R.id.header);
        msg = findViewById(R.id.msg);
        userList=findViewById(R.id.userLlist);
        last_admin= findViewById(R.id.last_admin_layout);

        int action = getIntent().getIntExtra("action", 0);

        pic.setImageResource((action == 0) ? android.R.drawable.ic_menu_delete : android.R.drawable.ic_menu_add);

        Object obj = getIntent().getSerializableExtra("obj");
        View v = null;

        if (obj instanceof Lock) {
            final Lock lock = (Lock) obj;
            header.setText("Delete " + lock.name);
            Log.d(TAG, "onCreate: only user situation");

            //checking if there's another users with this lock
            ArrayList<String> admin = new ArrayList<>(Arrays.asList(lock.admin.split(",")));
            if (admin.contains(FireBase.getUid()))
                admin.remove(FireBase.getUid());
            ArrayList<String> notAdimn = new ArrayList<>(Arrays.asList(lock.notAdmin.split(",")));
            if (notAdimn.contains(FireBase.getUid()))
                notAdimn.remove(FireBase.getUid());

            //setting the correct layout
            //last user
            Log.d(TAG, "onCreate: admin = " + admin.size() + " not = " + notAdimn.get(0).length());
            if (admin.size() == 0 && (notAdimn.size() == 0 || notAdimn.get(0).length() == 0)) {
                msg.setText(getString(R.string.dialog_lastUser));
                alertParam.topToBottom = R.id.msg;
                alertParam.topMargin = 8;
                last_admin.setVisibility(View.INVISIBLE);
            }


            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FireBase.deleteLock(lock, null);
                    finish();
                }
            });
        }
    }

}
