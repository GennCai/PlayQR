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
    private OnRecycleItemClickListener mItemClickListener;
    private OnRecycleItemLongClickListener mItemLongClickListener;
    public interface OnRecycleItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnRecycleItemLongClickListener{
        void onItemLongClick(View view, int position);
    }
    public RecyclerAdapter(List<Image> mData) {
        this.mData = mData;
    }

    public void setItemClickListener(OnRecycleItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;

    }
    public void setItemLongClickListener(OnRecycleItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
        ImageView fileIconView;
        TextView fileNameView;
        public ViewHolder(View view) {
            super(view);
           fileIconView = (ImageView) view.findViewById(R.id.file_icon_item);
           fileNameView = (TextView) view.findViewById(R.id.file_name_item);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
        //    Log.d("------Adapter-------", ": " + getLocation() + getAdapterPosition() + getLayoutPosition());
            if (mItemClickListener != null) {
                int pos = getLayoutPosition();
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(v, getLayoutPosition());
                return true;
            }
            return false;
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

