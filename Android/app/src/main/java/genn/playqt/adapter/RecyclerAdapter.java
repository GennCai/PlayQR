package genn.playqt.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import genn.playqt.R;
import genn.playqt.database.Image;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Image> mData;
    private OnRecycleItemClickListener recycleItemClickListener;
    public interface OnRecycleItemClickListener {
        void onItemClick(View view, int position);
    }

    public RecyclerAdapter(List<Image> mData) {
        this.mData = mData;
    }

    public void setRecycleItemClickListener(OnRecycleItemClickListener itemClickListener) {
        this.recycleItemClickListener = itemClickListener;

    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView fileIconView;
        TextView fileNameView;
        public ViewHolder(View view) {
            super(view);
           fileIconView = (ImageView) view.findViewById(R.id.file_icon_item);
           fileNameView = (TextView) view.findViewById(R.id.file_name_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        //    Log.d("------Adapter-------", ": " + getPosition() + getAdapterPosition() + getLayoutPosition());
            if (recycleItemClickListener != null) {
                int pos = getLayoutPosition();
                recycleItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    Image mImage;
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mImage = mData.get(position);
        holder.fileIconView.setImageBitmap(mImage.getIcon());
        holder.fileNameView.setText(mImage.getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}

