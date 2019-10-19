package packet.com.lockedappproject.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import packet.com.lockedappproject.Adapters.BlueAdapter;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.BlueThread;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;
import packet.com.lockedappproject.models.Lock;

public class bluetooth extends AppCompatActivity implements BlueAdapter.BlueCB, BlueThread.ThreadCB, FireBase.FindLock {

    private BluetoothAdapter bta;
    private BlueThread bThread;
    private BroadcastReceiver receiver;
    private TextView scan, sts, id, wifi, info;
    private CheckBox signed, wifiCheck;
    private BlueAdapter dAdapter;
    private RecyclerView dList;
    private Button button1, button2;
    private String lid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //Asking for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        //setting bluetooth adapter
        bta = BluetoothAdapter.getDefaultAdapter();
        if (!bta.isEnabled()) {
            // We need to enable the Bluetooth, so we ask the user
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        //register to get BT search results
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    System.out.println("testing ---> found " + device.getName() + ", " + device.getAddress());
                    dAdapter.addDevice(device);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        dAdapter = new BlueAdapter(getApplicationContext(), this);
        dList = findViewById(R.id.dList);
        dList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        dList.setAdapter(dAdapter);

        scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("testing ---> start scanning");

                if (bThread != null)
                    bThread.cancel();
                dAdapter.reset();
                sts.setText("Scanning...");
                bta.startDiscovery();
                info.setVisibility(View.INVISIBLE);
                button1.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.INVISIBLE);
            }
        });

        sts = findViewById(R.id.sts);
        id = findViewById(R.id.id);
        wifi = findViewById(R.id.wifi);
        signed = findViewById(R.id.signed);
        wifiCheck = findViewById(R.id.wifiCheck);
        info = findViewById(R.id.info);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length < 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "We need this permission to continue", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!bta.isEnabled()) {
            Toast.makeText(this, "We need Bluetooth to connect with the lock :/", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        try {
            bThread.cancel();
        } catch (Exception e) {
        }
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (bThread != null)
            bThread.cancel();
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        System.out.println("testing --> connecting to: " + device.getAddress());
        sts.setText("Connecting...");
        bThread = new BlueThread(device, this);
        bThread.start();
        info.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateUi(int status, String msg) {
        switch (status) {
            case DISCONNECT:
                sts.setText("Disconnect");
                id.setText("");
                wifi.setText("");
                signed.setChecked(false);
                wifiCheck.setChecked(false);
                info.setVisibility(View.INVISIBLE);
                button1.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.INVISIBLE);
                break;
            case CONNECTING:
                System.out.println("testing ---> CONNECTING CASE: " + msg + ".");
                if (!msg.equalsIgnoreCase("SYNC"))
                    bThread.write("1", CONNECTING);
                else {
                    //   sts.setText("Connected");
                    bThread.write(GET_LID + "", GET_LID);
                    sts.setText("Getting Lock details...");
                    info.setVisibility(View.INVISIBLE);
                }
                break;
            case GET_LID:
                System.out.println("testing ---> GET_LID reply");
                id.setText(msg);
                FireBase.searchGeneralLock(msg, this);
                sts.setText("Getting WIFI status...");
                bThread.write(GET_SSID + "", GET_SSID);
                break;
            case GET_SSID:
                wifi.setText(msg);
                lid = msg;
                bThread.write(GET_WIFI + "", GET_WIFI);
                break;
            case GET_WIFI:
                System.out.println("testing ---> GET_WIFI msg: " + msg);
                wifiCheck.setChecked(msg.equalsIgnoreCase("1"));
                sts.setText("Connected");
                button1.setVisibility((button1.getText().length() > 0) ? View.VISIBLE : View.INVISIBLE);
                button2.setVisibility((button2.getText().length() > 0) ? View.VISIBLE : View.INVISIBLE);
                break;
        }
    }

    @Override
    public void found(final House house, final Lock lock) {
        signed.setChecked(true);
        button2.setText("");
        if (FireBase.findLockInList(lock.id)) {
            System.out.println("testing ---> signed to this lock " + lock.id);
            if (lock.admin.contains(FireBase.getUid())) {
                button1.setText("SET WIFI");
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setWfi();
                    }
                });
                button1.setText("");
//                button1.setVisibility(View.VISIBLE);
            }
        } else {
            if (!lock.notAdmin.contains(FireBase.getUid())) {
                button1.setText("ADD LOCK");
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add(house, lock);
                    }
                });
            } else {
                button1.setText("");
            }
        }
    }

    @Override
    public void notFound(String lId) {
        signed.setChecked(false);
        button1.setText("SET WIFI");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWfi();
            }
        });

        button2.setText("ACTIVATE LOCK");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activate();
            }
        });
    }

    private void setWfi() {
        Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
        intent.putExtra("status", 5);
        startActivity(intent);
    }

    private void add(House house, Lock lock) {
        Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
        intent.putExtra("status", 2);
        intent.putExtra("houseName", house.name);
        intent.putExtra("lockName", lock.name);
        intent.putExtra("lockId", lock.id);
        intent.putExtra("lockAdmins", lock.admin);
        startActivity(intent);
//        finish();
//        Toast.makeText(this, "add()", Toast.LENGTH_SHORT).show();
    }

    private void activate() {
        Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
        intent.putExtra("status", 3);
        intent.putExtra("houseName", "New House");
        intent.putExtra("houseId", lid);
        startActivity(intent);
//        finish();
//        Toast.makeText(this, "active()", Toast.LENGTH_SHORT).show();
    }
}
