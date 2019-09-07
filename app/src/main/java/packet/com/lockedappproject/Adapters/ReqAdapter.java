package packet.com.lockedappproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;
import packet.com.lockedappproject.models.Lock;
import packet.com.lockedappproject.models.Req;
import packet.com.lockedappproject.models.User;

public class ReqAdapter extends RecyclerView.Adapter<ReqAdapter.ReqHolder> implements FireBase.UpdateRequests{

    private HashMap<String,Req> list;
    private ArrayList<String> requests;
    private Context context;

    public ReqAdapter(Context context) {
        this.context = context;
        list = FireBase.getRequests();
        requests = new ArrayList<>(list.keySet());

    }

    @NonNull
    @Override
    public ReqHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_req, parent,false);
        return new ReqHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReqHolder holder, int position) {
        Req req = list.get(requests.get(position));
        User user = FireBase.getAnotherUser(req.getFromUser());
        final Lock lock = FireBase.getLockByStr(req.getLockId());
        final House house = FireBase.getOneHouse(req.getHouseId());
        holder.nick.setText(user.nick);
        holder.lock.setText(lock.name);
        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "approve was clicked\nhouse = "+house.name+"\nlock = "+lock.name, Toast.LENGTH_SHORT).show();
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "reject was clicked\nlock = "+lock.name, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (requests != null && requests.size() > 0) ? requests.size() : 0;
    }

    @Override
    public void Notify(int size) {
        list = FireBase.getRequests();
        requests = new ArrayList<>(list.keySet());
        notifyDataSetChanged();
    }

    public class ReqHolder extends RecyclerView.ViewHolder {

        private TextView nick, lock;
        private CheckBox admin;
        private Button approve, reject;

        public ReqHolder(@NonNull View itemView) {
            super(itemView);
            nick = itemView.findViewById(R.id.nick);
            lock = itemView.findViewById(R.id.lock);
            approve = itemView.findViewById(R.id.approve);
            reject = itemView.findViewById(R.id.reject);
            admin = itemView.findViewById(R.id.admin);
        }
    }
}
