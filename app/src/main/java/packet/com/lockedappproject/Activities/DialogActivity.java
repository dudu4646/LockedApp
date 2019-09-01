package packet.com.lockedappproject.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;
import packet.com.lockedappproject.models.Lock;
import packet.com.lockedappproject.models.User;

public class DialogActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button ok, cancel;
    private ConstraintLayout takenLayout, foundLayout, newLockLayout, dltLockLayout, newHouseLayout;
    private TextView header;
    private EditText addHouseName, addHouseStreet, addHouseAddress, newLockName;
    private ImageView img;
    private Spinner spinner;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);

        //Buttons
        ok = findViewById(R.id.ok);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //ConstraintLayout
        takenLayout = findViewById(R.id.takenLayout);
        foundLayout = findViewById(R.id.foundLayout);
        newLockLayout = findViewById(R.id.newLockLayout);
        dltLockLayout = findViewById(R.id.dltLockLayout);
        newHouseLayout = findViewById(R.id.newHouseLayout);
        //TextView
        header = findViewById(R.id.header);
        //ImageView
        img = findViewById(R.id.img);
        //Spinner
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        //CheckBox
        checkBox = findViewById(R.id.checkBox);
        //EditText
        addHouseName = findViewById(R.id.addHouseName);
        addHouseName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = editable.toString().trim();
                if(name.length()==0)
                    addHouseName.setError("Cant' remain empty...");
                else{
                    boolean flg=false;
                    ArrayList<House> houses=FireBase.getHouses();
                    for (House h:houses){
                        if(h.name.equalsIgnoreCase(name)){
                            flg=true;
                            newLockName.setError("This name already taken, Try another one");
                            break;
                        }
                    }
                    if (!flg)
                        addHouseName.setError(null);
                }
            }
        });
        addHouseStreet = findViewById(R.id.addHouseStreet);
        addHouseStreet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length()==0)
                    addHouseStreet.setError("Cant' remain empty...");
                else
                    addHouseStreet.setError(null);
            }
        });
        addHouseAddress = findViewById(R.id.addHouseAddress);
        addHouseAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length()==0)
                    addHouseAddress.setError("Cant' remain empty...");
                else
                    addHouseAddress.setError(null);
            }
        });
        newLockName = findViewById(R.id.newLockName);
        newLockName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString().trim();
                if (str.length() == 0)
                    newLockName.setError("Cant' remain empty...");
                else {
                    ArrayList<Lock> locks = FireBase.getLockes();
                    boolean flg = false;
                    for (Lock l : locks) {
                        if (l.name.equalsIgnoreCase(str)) {
                            newLockName.setError("This name already taken, Try another one");
                            flg = true;
                            break;
                        }
                    }
                    if (!flg)
                        newLockName.setError(null);
                }
            }
        });

        int status = getIntent().getIntExtra("status", 0);

        switch (status) {
            //user already signed to lock
            case 1:
                header.setText("Lock already exists");
                takenLayout.setVisibility(View.VISIBLE);
                TextView msg1 = findViewById(R.id.msg1);
                msg1.setText(R.string.lockexistsP1);
                TextView houseName = findViewById(R.id.houseName);
                houseName.setText(getIntent().getStringExtra("houseName"));
                houseName.setTypeface(Typeface.DEFAULT_BOLD);
                TextView msg2 = findViewById(R.id.msg2);
                msg2.setText(R.string.lockexistsP2);
                TextView lockName = findViewById(R.id.newLock);
                lockName.setTypeface(Typeface.DEFAULT_BOLD);
                lockName.setText(getIntent().getStringExtra("lockName"));
                cancel.setVisibility(View.GONE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
                break;
            //found lock in another house:
            case 2:
                header.setText("Add lock");
                img.setImageResource(R.drawable.add);
                foundLayout.setVisibility(View.VISIBLE);
                TextView found2 = findViewById(R.id.found2);
                found2.setText(getIntent().getStringExtra("houseName"));
                found2.setTypeface(Typeface.DEFAULT_BOLD);
                TextView found4 = findViewById(R.id.found4);
                found4.setText(getIntent().getStringExtra("lockName"));
                found4.setTypeface(Typeface.DEFAULT_BOLD);

                final String lockAdmins = getIntent().getStringExtra("lockAdmins");
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(DialogActivity.this, "שליחת בקשה לאדמינים של המנעול", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                break;
            //new lock
            case 3:
                User user = FireBase.getUser();
                ArrayList<String> arr = new ArrayList<>();
                arr.add("New House");
                ArrayList<House> houses = FireBase.getHouses();
                for (House h : houses)
                    arr.add(h.name);
                ArrayAdapter<String> spinAdpat = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, arr);
                spinner.setAdapter(spinAdpat);
                String toHouse = getIntent().getStringExtra("houseName");
                spinner.setSelection(arr.indexOf(toHouse));
                header.setText("Add lock");
                img.setImageResource(R.drawable.add);
                newLockLayout.setVisibility(View.VISIBLE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkData()) {
                            addLock(spinner.getSelectedItemPosition());
                            Intent intent = new Intent();
                            setResult(1, intent);
                            finish();
                        } else
                            Snackbar.make(ok, "There's some data missing...", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                    }
                });
                break;
            //delete house
            case 4:
                header.setText("Delete lock");
                img.setImageResource(R.drawable.dlt);
                dltLockLayout.setVisibility(View.VISIBLE);
                final String lName = getIntent().getStringExtra("lockName");
                TextView dltLockName = findViewById(R.id.dltLockName);
                dltLockName.setText(lName);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FireBase.deleteLock(lName);
                        Intent intent = new Intent();
                        setResult(2, intent);
                        finish();
                    }
                });
                break;
        }
    }

    private boolean checkData() {
        boolean name = (newLockName.getError() == null && newLockName.getText().length() > 0);
        boolean house = true;
        if ((newHouseLayout.getVisibility() == View.VISIBLE) &&
                (addHouseName.getError() != null || addHouseName.getText().length() == 0 ||
                        addHouseStreet.getError() != null || addHouseStreet.getText().length() == 0 ||
                        addHouseAddress.getError() != null || addHouseAddress.getText().length() == 0))
            house = false;
        return name && house;
    }

    @Override
    public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) {
        if (i != 0) {
            newHouseLayout.setVisibility(View.GONE);
        } else {
            newHouseLayout.setVisibility(View.VISIBLE);
            addHouseAddress.setText("");
            addHouseStreet.setText("");
            addHouseName.setText("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        if (spinner.getSelectedItemPosition() == 0) {
            newHouseLayout.setVisibility(View.VISIBLE);
            addHouseAddress.setText("");
            addHouseStreet.setText("");
            addHouseName.setText("");
        }
    }

    private void addLock(int position) {
        //יצירה של המנעול החדש
        String name = newLockName.getText().toString().trim();
        String admin = "";
        String notAdmin = "";
        if (checkBox.isChecked())
            admin = FireBase.getUid();
        else
            notAdmin = FireBase.getUid();
        Lock lock = new Lock(name, "open", getIntent().getStringExtra("houseId"), admin, notAdmin);

        if (position != 0) {
            House house = FireBase.getHouses().get(position - 1);
            FireBase.addNewLock(lock, house);
            finish();
        } else {
            Toast.makeText(this, "לבנות הוספה של בית ומנעול חדש", Toast.LENGTH_SHORT).show();

        }
    }

}
