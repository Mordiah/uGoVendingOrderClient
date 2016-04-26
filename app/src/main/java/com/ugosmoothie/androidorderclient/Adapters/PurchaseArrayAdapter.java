package com.ugosmoothie.androidorderclient.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.ugosmoothie.androidorderclient.Order;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Michelle on 3/19/2016.
 */
public class PurchaseArrayAdapter extends ArrayAdapter<Order> {

    HashMap<Order, Long> mIdMap = new HashMap<>();

    public PurchaseArrayAdapter(Context context, int textViewResourceId,
                              List<Order> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), objects.get(i).getId());
        }
    }

    @Override
    public long getItemId(int position) {
        if (position > getCount()) {
            return 0;
        }

        Order item = getItem(position);
        return item.getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
