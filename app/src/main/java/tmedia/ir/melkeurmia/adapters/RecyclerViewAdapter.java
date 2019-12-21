package tmedia.ir.melkeurmia.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import tmedia.ir.melkeurmia.R;
import tmedia.ir.melkeurmia.model.AbstractModel;
import tmedia.ir.melkeurmia.tools.CONST;


/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<AbstractModel> modelList;
    private OnItemClickListener mItemClickListener;

    public void updateList(ArrayList<AbstractModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }


    public RecyclerViewAdapter(Context context, ArrayList<AbstractModel> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final AbstractModel model = getItem(position);
            ViewHolder genericViewHolder = (ViewHolder) holder;
            genericViewHolder.itemTxtTitle.setText(model.getTitle());
            Glide.with(mContext)
                    .load(CONST.STORAGE + "cat_img/" + model.getPath()).into(genericViewHolder.imgUser);

        }
    }


    @Override
    public int getItemCount() {

        return modelList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position, AbstractModel model);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    private AbstractModel getItem(int position) {
        return modelList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgUser;
        private TextView itemTxtTitle;
        public ViewHolder(final View itemView) {
            super(itemView);
            this.imgUser = (ImageView) itemView.findViewById(R.id.img_user);
            this.itemTxtTitle = (TextView) itemView.findViewById(R.id.item_txt_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));
                }
            });


        }
    }
}

