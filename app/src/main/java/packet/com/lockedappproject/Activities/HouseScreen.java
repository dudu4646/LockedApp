package packet.com.lockedappproject.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import packet.com.lockedappproject.Adapters.HouseScreenAdapt;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;
import packet.com.lockedappproject.models.Lock;

public class HouseScreen extends AppCompatActivity implements HouseScreenAdapt.Interface, FireBase.UpdateUi {

    private TextView h1, h2, checkNum;
    private RecyclerView lockList;
    private ImageView open, close, admin, addImg, dltImg;
    private HouseScreenAdapt adapt;
    private House house;
    private List<Lock> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_screen);

        //House + adapt
        house = FireBase.getOneHouse(getIntent().getStringExtra("houseId"));
        adapt = new HouseScreenAdapt(getApplicationContext(), house.locks, this);
        //TextView
        h1 = findViewById(R.id.h1);
        h1.setText(house.name);
        h2 = findViewById(R.id.h2);
        h2.setText(house.address);
        checkNum = findViewById(R.id.checkNum);
        //RecycleView
        lockList = findViewById(R.id.lockList);
        lockList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        lockList.setAdapter(adapt);
        //List<Lock>
        arr = new ArrayList<>();
        //ImageView
        open = findViewById(R.id.opemImg);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arr.size() == 0)
                    Snackbar.make(view, "Choose Lock to open", Snackbar.LENGTH_SHORT);
                else {
                    boolean flg = false;
                    for (Lock l : arr) {
                        boolean admin = l.admin.contains(FireBase.getUid());
                        if (l.status.equalsIgnoreCase("close") ||
                                (l.status.equalsIgnoreCase("lock") && admin))
                            FireBase.changeLockTo(l, "open");
                        else
                            flg = (l.status.equalsIgnoreCase("lock") && !admin) || flg;
                    }
                    if (flg)
                        Snackbar.make(view, R.string.generalLockAlert, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        close = findViewById(R.id.closeImg);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arr.size() == 0)
                    Snackbar.make(view, "Choose Lock to open", Snackbar.LENGTH_SHORT);
                else {
                    boolean flg = false;
                    for (Lock l : arr) {
                        boolean admin = l.admin.contains(FireBase.getUid());
                        if ((l.status.equalsIgnoreCase("open")) ||
                                (l.status.equalsIgnoreCase("lock") && admin))
                            FireBase.changeLockTo(l, "close");
                        else
                            flg = (l.status.equalsIgnoreCase("lock") && !admin) || flg;
                    }
                    if (flg)
                        Snackbar.make(view, R.string.missingAdminPer, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        admin = findViewById(R.id.adminImg);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arr.size() == 0)
                    Snackbar.make(view, "Choose Lock to open", Snackbar.LENGTH_SHORT);
                else {
                    boolean flg = false;
                    for (Lock l : arr) {
                        boolean admin = l.admin.contains(FireBase.getUid());
                        if ((!l.status.equalsIgnoreCase("lock")) && admin)
                            FireBase.changeLockTo(l, "lock");
                        else
                            flg = !admin || flg;
                    }
                    if (flg)
                        Snackbar.make(view, R.string.missingAdminPer, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        addImg = findViewById(R.id.addImg);
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddLock.class);
                intent.putExtra("houseId", house.id);
                startActivity(intent);

            }
        });
        dltImg = findViewById(R.id.dltImg);
        dltImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arr.size() != 1)
                    Snackbar.make(view, ((arr.size() == 0) ? R.string.nothingToDelete : R.string.manyToDelete), Snackbar.LENGTH_LONG).show();
                else {
                    Toast.makeText(HouseScreen.this, "activate FireBase.deleteLockFromUser()", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
                    intent.putExtra("lockName", arr.get(0).name);
                    intent.putExtra("status", 4);
                    startActivity(intent);

                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FireBase.addToUpdateLocks(adapt);
    }

    @Override
    protected void onStop() {
        FireBase.removeFromUpdateLock(adapt);
        super.onStop();
    }

    @Override
    public void handleLock(View view, int size) {
        TextView tv = (TextView) view;
        Lock l = FireBase.getLockByStr(tv.getText().toString());
        int i;
        for (i = 0; i < arr.size() && !arr.get(i).name.equalsIgnoreCase(tv.getText().toString()); i++)
            ;
        if (i < arr.size()) {
            tv.setBackgroundColor(Color.TRANSPARENT);
            tv.setTypeface(Typeface.DEFAULT);
            arr.remove(i);
        } else {
            tv.setBackgroundColor(Color.LTGRAY);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            arr.add(FireBase.getLockByStr(tv.getText().toString()));
        }
        checkNum.setText((arr.size() == 0) ? "" : arr.size() + "/" + size);
    }

    @Override
    public void Success() {
        arr = new ArrayList<>();
        checkNum.setText("");
    }

    @Override
    public void Failed(Exception e) {

    }
}

