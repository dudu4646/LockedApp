package packet.com.lockedappproject.Adapters;


import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;
import packet.com.lockedappproject.models.Lock;

public class HouseCard extends RecyclerView.Adapter<HouseCard.CardHouseHolder> implements FireBase.UpdateHouseData {

    private List<House> houses;
    private Context context;
    private Go_Add_To callBack;

    public HouseCard(List<House> houses, Context context, Go_Add_To callBack) {
        this.houses = houses;
        this.context = context;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public CardHouseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_house_card, viewGroup, false);
        return new CardHouseHolder(view,callBack);
    }

    @Override
    public void onBindViewHolder(@NonNull final CardHouseHolder holder, int position) {
        final House house = houses.get(position);
        holder.cbHouse = house;
        holder.locks = FireBase.getLockFromList(house.locks);
        holder.adapt = new LockCard(holder.locks, context, house);
        FireBase.addToUpdateLocks(holder.adapt);
        holder.lockList.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        holder.lockList.setAdapter(holder.adapt);
        holder.name.setText(house.name);
        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.isOpen) {
                    holder.exLayout.animate().alpha(0f).setDuration(300).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            holder.exLayout.setVisibility(holder.exLayout.getAlpha() == 1f ? View.VISIBLE : View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    }).start();
                    holder.isOpen = false;
                } else {
                    holder.exLayout.setAlpha(0f);
                    holder.exLayout.animate().alpha(1f).setDuration(500).start();
                    holder.exLayout.setVisibility(View.VISIBLE);
                    holder.isOpen = true;
                }
            }
        });
        holder.address.setText(house.address);
    }


    @Override
    public int getItemCount() {
        return (houses != null && houses.size() > 0) ? houses.size() : 0;
    }

    @Override
    public void Notify() {
        houses = FireBase.getHouses();
        notifyDataSetChanged();
    }

    public interface Go_Add_To {
        void goTO(House house);
        void addTo(House house);
    }

    static class CardHouseHolder extends RecyclerView.ViewHolder {
        private TextView name, address;
        private RecyclerView lockList;
        private boolean isOpen;
        private List<Lock> locks;
        private LockCard adapt;
        private ConstraintLayout cardLayout, exLayout;
        private ImageView goImg, addImg;
        private House cbHouse;

        private CardHouseHolder(@NonNull final View itemView,final Go_Add_To cb) {
            super(itemView);
            name = itemView.findViewById(R.id.cardName);
            address = itemView.findViewById(R.id.cardAddress);
            lockList = itemView.findViewById(R.id.lockCardList);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            exLayout = itemView.findViewById(R.id.exLayout);
            isOpen = false;
            goImg = itemView.findViewById(R.id.getImg);
            goImg.setImageResource(R.drawable.h);
            goImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.goTO(cbHouse);
                }
            });
            addImg = itemView.findViewById(R.id.addImg);
            addImg.setImageResource(R.drawable.add);
            addImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.addTo(cbHouse);
                }
            });
        }
    }
}
