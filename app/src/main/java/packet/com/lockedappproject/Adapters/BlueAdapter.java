package packet.com.lockedappproject.Adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import packet.com.lockedappproject.R;

public class BlueAdapter extends RecyclerView.Adapter<BlueAdapter.DeviceHolder> {
    private List<BluetoothDevice> devices;
    private Set<BluetoothDevice> paired;
    private Context context;
    private BlueCB cb;

    public BlueAdapter(Context context, BlueCB cb) {
        devices = new ArrayList<>();
        paired = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        this.context = context;
        this.cb = cb;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_blue_device, parent, false);
        return new DeviceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
        final BluetoothDevice device = devices.get(position);
        holder.dName.setText((device.getName() != null && device.getName().length() > 0) ? device.getName() : device.getAddress());
        holder.holderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.connect(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addDevice(BluetoothDevice device) {
        BluetoothDevice toAdd = device;
        for (BluetoothDevice d : paired)
            if (d.getAddress().equalsIgnoreCase(device.getAddress())) {
                toAdd = d;
                break;
            }
        devices.add(toAdd);
        System.out.println("testing ---> adding new device");
        notifyDataSetChanged();
    }

    public void reset() {
        devices = new ArrayList<>();
        notifyDataSetChanged();
    }

    public interface BlueCB {
        void connect(BluetoothDevice device);
    }

    static class DeviceHolder extends RecyclerView.ViewHolder {
        private TextView dName, dConnect;
        private ConstraintLayout holderLayout;

        public DeviceHolder(@NonNull View itemView) {
            super(itemView);
            dName = itemView.findViewById(R.id.dName);
            dConnect = itemView.findViewById(R.id.dConnect);
            holderLayout = itemView.findViewById(R.id.holderLyout);
        }
    }
}
