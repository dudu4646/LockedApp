package packet.com.lockedappproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;
import packet.com.lockedappproject.models.Lock;

public class LockCard extends RecyclerView.Adapter<LockCard.CardLockHolder> implements FireBase.UpdateLockData {

    private List<Lock> locks;
    private Context context;
    private House house;
    private UpdateLockNum cb;

    public LockCard(List<Lock> locks, Context context, House house,UpdateLockNum cb) {
        this.locks = locks;
        this.context = context;
        this.house=house;
        this.cb=cb;
    }

    @NonNull
    @Override
    public CardLockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_lock_card, parent, false);
        return new CardLockHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardLockHolder holder, int position) {
        final Lock lock = locks.get(position);
        holder.name.setText(lock.name.replace(' ','\n'));
        switch (lock.status) {
            case "open":
                holder.img.setImageResource(R.drawable.open_2);
                break;
            case "close":
                holder.img.setImageResource(R.drawable.close_2);
                break;
            case"lock":
                holder.img.setImageResource(R.drawable.admin_2);
                break;
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lock.status.equalsIgnoreCase("lock")){
                    Snackbar.make(view,R.string.lockedalert,Snackbar.LENGTH_LONG).show();
                }else{
                    FireBase.changeLockTo(lock,(lock.status.equalsIgnoreCase("open"))?"close":"open");
                }
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.img.callOnClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (locks != null && locks.size() > 0) ? locks.size() : 0;
    }

    @Override
    public void Notify() {
        locks = FireBase.getLockFromList(house.locks);
        cb.updateLocksNum(getItemCount());
        notifyDataSetChanged();
    }

    public interface UpdateLockNum{
        void updateLocksNum(int size);
    }

    static class CardLockHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView img;

        public CardLockHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.newLock);
            img = itemView.findViewById(R.id.img);
        }
    }
}
