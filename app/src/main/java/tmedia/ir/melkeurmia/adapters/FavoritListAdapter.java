package tmedia.ir.melkeurmia.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.model.Favorit;

public class FavoritListAdapter extends ArrayAdapter<Favorit> {
    private ArrayList<Favorit> items;

    private onItemInnerClick  mListener;
    public FavoritListAdapter(@NonNull Context context, ArrayList<Favorit> items , onItemInnerClick listener ) {
        super(context, 0, items);
        mListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Favorit item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorit_list_item_row, parent, false);
        }
        // Lookup view for data population
        TextView tvName =  convertView.findViewById(R.id.tv);
        LinearLayout view_container =  convertView.findViewById(R.id.view_container);
        Button delete_btn =  convertView.findViewById(R.id.delete_btn);
        ImageView arrowView = (ImageView) convertView.findViewById(R.id.arrowView);
        // Populate the data into the template view using the data object
        tvName.setText(item.getName());
        // Return the completed view to render on screen

        view_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewClick(item.getName(), item.getPath(), item.getId());
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRemoveClick(item.getName(), item.getPath(), item.getId());
            }
        });


        return convertView;
    }

    public interface onItemInnerClick{
        void onRemoveClick(String title, String path, String id);
        void onViewClick(String title, String path, String id);
    }

}
