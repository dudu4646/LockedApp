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

    //    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter bta;
    private BlueThread bThread;
    private BroadcastReceiver receiver;
    private TextView scan, sts, id, wifi;
    private CheckBox signed, wifiCheck;
    private BlueAdapter dAdapter;
    private RecyclerView dList;

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
                sts.setText("");
                bta.startDiscovery();
            }
        });

        sts = findViewById(R.id.sts);
        id = findViewById(R.id.id);
        wifi = findViewById(R.id.wifi);
        signed = findViewById(R.id.signed);
        wifiCheck = findViewById(R.id.wifiCheck);
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
        System.out.println("testing --> connecting to: " + device.getAddress());
        bThread = new BlueThread(device, this);
        System.out.println("testing ---? flg");
        bThread.start();
    }


    @Override
    public void updateUi(int status, String msg) {
        switch (status) {
            case DISCONNECT:
                sts.setText("Disconnect");
                bThread.cancel();
                break;
            case CONNECTING:
                sts.setText("Connecting...");
                break;
            case CONNECTED:
                sts.setText("Connected");
                bThread.write((GET_LID + "").getBytes(), GET_LID);
                sts.setText("Getting Lock details...");
                break;
            case GET_LID:
                System.out.println("testing ---> GET_LID reply");
                id.setText(msg);
                FireBase.searchGeneralLock(msg, this);
                sts.setText("Getting WIFI status...");
                bThread.write((GET_SSID + "").getBytes(), GET_SSID);
                break;
            case GET_SSID:
                wifi.setText(msg);
                bThread.write((GET_WIFI + "").getBytes(), GET_WIFI);
                break;
            case GET_WIFI:
                System.out.println("testing ---> GET_WIFI msg: " + msg);
                wifiCheck.setChecked(msg.equalsIgnoreCase("1"));
                break;
        }
    }

    @Override
    public void found(House house, Lock lock) {
        signed.setChecked(true);
    }

    @Override
    public void notFound(String lId) {
        signed.setChecked(false);
    }
}
