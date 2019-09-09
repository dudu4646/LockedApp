package packet.com.lockedappproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import packet.com.lockedappproject.Activities.DialogActivity;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.Lock;


public class HouseScreenAdapt extends RecyclerView.Adapter<HouseScreenAdapt.LockHolder> implements FireBase.UpdateLockData{

    private Context context;
    private List<Lock> locks;
    private String lockStr;
    private Interface cb;

    public HouseScreenAdapt(Context context, String lockStr, Interface cb) {
        this.context = context;
        this.lockStr = lockStr;
        this.locks = FireBase.getLockFromList(lockStr);
        this.cb=cb;
    }

    @NonNull
    @Override
    public LockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_locks, parent, false);
        return new LockHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LockHolder holder, int position) {
        final Lock lock = locks.get(position);
        holder.cb.setChecked(lock.admin.contains(FireBase.getUid()));
        holder.tv.setText(lock.name);
        switch (lock.status) {
            case "open":
                holder.iv.setImageResource(R.drawable.open);
                break;
            case "close":
                holder.iv.setImageResource(R.drawable.close);
                break;
            case "lock":
                holder.iv.setImageResource(R.drawable.alock);
                break;
        }
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.handleLock(view,locks.size());
            }
        });

        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lock.status.equalsIgnoreCase("lock")) {
                    Snackbar.make(view, R.string.lockedalert, Snackbar.LENGTH_LONG).show();
                } else {
                    FireBase.changeLockTo(lock, (lock.status.equalsIgnoreCase("open")) ? "close" : "open");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (locks != null && locks.size() > 0) ? locks.size() : 0;
    }

    @Override
    public void Notify() {
        System.out.println("testing ---> "+getClass().getName()+" Notify() activated");
        this.locks = FireBase.getLockFromList(lockStr);
        notifyDataSetChanged();
    }


    public interface Interface {
        void handleLock(View view, int size);
    }

    static class LockHolder extends RecyclerView.ViewHolder {

        private ImageView iv;
        private CheckBox cb;
        private TextView tv;

        public LockHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            cb = itemView.findViewById(R.id.cb);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}

