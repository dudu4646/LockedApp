package packet.com.lockedappproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import packet.com.lockedappproject.Adapters.HouseCard;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;

public class HomePageActivity extends AppCompatActivity implements HouseCard.Go_Add_To, FireBase.UpdateRequests {

    private RecyclerView cardsView;
    private List<House> houseList;
    private HouseCard adapt;
    private ConstraintLayout newLockLayout;
    private CardView reqCard;
    private TextView reqNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        houseList = FireBase.getHouses();
        cardsView = findViewById(R.id.cardsView);
        adapt = new HouseCard(houseList, getApplicationContext(), this);
        cardsView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        cardsView.setAdapter(adapt);
        newLockLayout = findViewById(R.id.NewLockLayout);
        newLockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddLock.class);
                intent.putExtra("houseName", "new House");
                startActivity(intent);
            }
        });
        reqNum = findViewById(R.id.reqNum);
        reqNum.setText(FireBase.getReqNum());
        reqCard = findViewById(R.id.reqCard);
        reqCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        FireBase.addToUpdateHouse(adapt);
        FireBase.addToRequestsUpdates(this);
        super.onStart();
    }

    @Override
    protected void onPause() {
        System.out.println("testing ---> onPause called");
        super.onPause();
        FireBase.removeFromUpdateHouse(adapt);
        FireBase.removeFromRequestsUpdates(this);
    }

    @Override
    public void goTO(House house) {

        Intent intent = new Intent(getApplicationContext(), HouseScreen.class);
        intent.putExtra("houseId", house.id);
        startActivity(intent);
    }

    @Override
    public void addTo(House house) {
        Intent intent = new Intent(getApplicationContext(), AddLock.class);
        intent.putExtra("houseName", house.name);
        startActivity(intent);
    }

    @Override
    public void Notify(int size) {
        System.out.println("testing ---> Notify called");
        System.out.println("testing ---> srtNum ---> size = "+size);
        reqNum.setText((size > 0) ? size + "" : "");
    }

}
