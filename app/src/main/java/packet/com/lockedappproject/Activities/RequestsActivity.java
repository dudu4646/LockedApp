package packet.com.lockedappproject.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import packet.com.lockedappproject.Adapters.ReqAdapter;
import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;

public class RequestsActivity extends AppCompatActivity implements ReqAdapter.CB {

    private RecyclerView reqList;
    private ReqAdapter reqAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        //Adapter
        reqAdapter = new ReqAdapter(getApplicationContext(), this);

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

    @Override
    public void close() {
        finish();
    }
}
