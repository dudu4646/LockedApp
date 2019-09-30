package packet.com.lockedappproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import packet.com.lockedappproject.Adapters.HouseCard;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;

public class HomePageActivity extends AppCompatActivity implements HouseCard.Go_Add_To, FireBase.UpdateRequests {

    private RecyclerView cardsView;
    private List<House> houseList;
    private HouseCard adapt;
    private ConstraintLayout newLockLayout,reqLayout,blueLayot;
//    private CardView reqCard,blueCard;
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
        reqLayout = findViewById(R.id.reqLayout);
        reqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reqNum.getText().toString().equalsIgnoreCase(""))
                    Snackbar.make(view, "You don't have any Requests waiting", Snackbar.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
            }
        });
        reqNum = findViewById(R.id.reqNum);
        blueLayot=findViewById(R.id.blueLayout);
        blueLayot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "לבנות עמוד התחברות עם bluetooth", Toast.LENGTH_SHORT).show();
            }
        });
//        blueCard = findViewById(R.id.blueCard);
//        blueCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "לבנות עמוד התחברות עם bluetooth", Toast.LENGTH_SHORT).show();
//            }
//        });
//        reqCard = findViewById(R.id.reqCard);
//        reqCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (reqNum.getText().toString().equalsIgnoreCase(""))
//                    Snackbar.make(view, "You don't have any Requests waiting", Snackbar.LENGTH_SHORT).show();
//                else
//                    startActivity(new Intent(getApplicationContext(), RequestsActivity.class));
//            }
//        });

    }

    @Override
    protected void onResume() {
        FireBase.addToRequestsUpdates(this);
        FireBase.addToUpdateHouse(adapt);
        adapt.Notify();

        Notify(FireBase.getReqNum());
        super.onResume();
    }

    @Override
    protected void onPause() {
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
        if (size==0)
            reqNum.setVisibility(View.INVISIBLE);
        else{
            reqNum.setVisibility(View.VISIBLE);
            reqNum.setText((size > 0) ? size + "" : "");
        }
    }
}
