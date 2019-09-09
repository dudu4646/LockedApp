package packet.com.lockedappproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import packet.com.lockedappproject.R;
import packet.com.lockedappproject.models.FireBase;
import packet.com.lockedappproject.models.House;

public class HouseAdapter extends ArrayAdapter implements FireBase.UpdateHouseData {

    private static final String TAG = "HouseAdapter";

    private int resLayout;
    private LayoutInflater layoutInflater;
    private ArrayList<House> houses;
    private HouseAdapt callback;

    public HouseAdapter(Context context, int resource, ArrayList<House> houses, HouseAdapt callback) {
        super(context, resource);
        this.resLayout = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.houses = houses;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return houses.size();
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

        viewHolder.tv.setText(houses.get(position).name);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onHouseClick(houses.get(position).id,v);
            }
        });
        return convertView;
    }

    @Override
    public void Notify() {
        System.out.println("testing ---> HouseAdapter Notify() called");
        notifyDataSetChanged();
    }

    public interface HouseAdapt {
        void onHouseClick(String h, View v);
    }

    private class ViewHolder {
        private TextView tv;

        private ViewHolder(View v) {
            this.tv = v.findViewById(R.id.tv);
        }
    }
}
