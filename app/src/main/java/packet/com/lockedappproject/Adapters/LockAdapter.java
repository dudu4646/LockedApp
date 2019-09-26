package packet.com.lockedappproject.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;
import packet.com.lockedappproject.models.Lock;

public class LockAdapter extends ArrayAdapter implements FireBase.UpdateLockData, Serializable {

    private static final String TAG = "dudu";

    private int resLayout;
    private LayoutInflater layoutInflater;
    private ArrayList<Lock> locks;
    private LockAdapt callback;
    private String inHouse;

    public LockAdapter(Context context, int resource, ArrayList<Lock> locks,LockAdapt callback,String inHouse) {
        super(context, resource);
        this.resLayout = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.locks = locks;
        this.callback = callback;
        this.inHouse =inHouse;
    }


    @Override
    public int getCount() {
        return locks.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(resLayout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tv.setText(locks.get(position).name);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onLockClick(locks.get(position),v);
            }
        });
        return convertView;
    }


    public void setData(ArrayList<Lock> src) {
        this.locks = src;
        notifyDataSetChanged();
    }

    public void setInHouse(String inHouse) {
        this.inHouse = inHouse;
    }

    @Override
    public void Notify() {
        Log.d(TAG, "Notify: for locks called");
        House house = FireBase.getOneHouse(inHouse);
        locks = FireBase.getLockFromList(house.locks);
        notifyDataSetChanged();
        Log.d(TAG, "Notify: for locks ended");
    }

    public interface LockAdapt{
        void onLockClick(Lock l, View v);
    }



    private class ViewHolder {
        private TextView tv;

        private ViewHolder(View v) {
            this.tv = v.findViewById(R.id.tv);
        }
    }

}
