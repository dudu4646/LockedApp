package packet.com.lockedappproject.Activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class HouseScreen extends AppCompatActivity implements HouseScreenAdapt.Interface {

    private TextView h1, h2, checkNum;
    private RecyclerView lockList;
    private Button closeAll, openAll, closeAdmin, openAdmin;
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
        //Button
        openAll = findViewById(R.id.openAll);
        openAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arr.size() == 0)
                    Snackbar.make(view, "Select locks first", Snackbar.LENGTH_SHORT).show();
                else {
                    boolean flg = false;
                    for (Lock l : arr)
                        if (l.status.equalsIgnoreCase("lock"))
                            flg = false;
                        else if (l.status.equalsIgnoreCase("close"))
                            FireBase.changeLockTo(l, "open");
                    if (flg)
                        Snackbar.make(view, R.string.generalLockAlert, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        closeAll = findViewById(R.id.closaAll);
        closeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arr.size() == 0)
                    Snackbar.make(view, "Select locks first", Snackbar.LENGTH_SHORT).show();
                else {
                    for (Lock l : arr)
                        if (l.status.equalsIgnoreCase("open"))
                            FireBase.changeLockTo(l, "close");
                }
            }
        });
        closeAdmin = findViewById(R.id.closeAdmin);
        closeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arr.size() == 0) {
                    Snackbar.make(view, "Select locks first", Snackbar.LENGTH_SHORT).show();
                } else {
                    boolean flg = false;
                    for (Lock l : arr)
                        if (l.admin.contains(FireBase.getUid()) && !l.status.equalsIgnoreCase("lock"))
                            FireBase.changeLockTo(l, "lock");
                        else
                            flg = (!l.admin.contains(FireBase.getUid()) || flg);
                    if (flg)
                        Snackbar.make(view, R.string.missingAdminPer, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        openAdmin = findViewById(R.id.openAdmin);
        openAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (arr.size()==0)
                  Snackbar.make(view, "Select locks first", Snackbar.LENGTH_SHORT).show();
              else{
                  boolean flg =false;
                  for (Lock l:arr)
                      if (l.admin.contains(FireBase.getUid()) && l.status.equalsIgnoreCase("lock"))
                          FireBase.changeLockTo(l,"open");
                      else
                          flg = (!l.admin.contains(FireBase.getUid()) || flg);
                  if (flg)
                      Snackbar.make(view, R.string.missingAdminPer, Snackbar.LENGTH_LONG).show();
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
        for (i=0;i<arr.size() && !arr.get(i).name.equalsIgnoreCase(tv.getText().toString());i++);
        if (i<arr.size()) {
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

}

