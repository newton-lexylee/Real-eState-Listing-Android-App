package tmedia.ir.melkeurmia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.model.ProviceModel;

/**
 * Created by tmedia on 7/9/2018.
 */

public class SpinnerAdapter extends ArrayAdapter<ProviceModel> {
    Context context;
    int flags[];
    List<ProviceModel> items;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext, int textViewResourceId, List<ProviceModel> _items) {
        super(applicationContext, textViewResourceId, _items);
        this.context = applicationContext;
        this.items = _items;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ProviceModel getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View row = LayoutInflater.from(context).inflate(R.layout.custom_spinner_items, viewGroup, false);
        TextView tv=(TextView)row.findViewById(R.id.tv);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(10,10,10,10);
        tv.setText(items.get(position).getName());
        return tv;
    }


    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setPadding(10,10,10,10);
        label.setText(items.get(position).getName());

        return label;
    }
}
