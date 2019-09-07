package packet.com.lockedappproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import packet.com.lockedappproject.Adapters.ReqAdapter;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;

public class RequestsActivity extends AppCompatActivity {

    private RecyclerView reqList;
    private ReqAdapter reqAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        //Adapter
        reqAdapter = new ReqAdapter(getApplicationContext());

        //RecycleView
        reqList = findViewById(R.id.reqList);
        reqList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        reqList.setAdapter(reqAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FireBase.addToRequestsUpdates(reqAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FireBase.removeFromRequestsUpdates(reqAdapter);
    }
}
