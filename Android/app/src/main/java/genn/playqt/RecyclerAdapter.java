package genn.playqt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<PhotoInfo> mData;

    private OnRecycleItemClickListener recycleItemClickListener;
    public interface OnRecycleItemClickListener {
        void onItemClick(View view, int position);
    }

    public RecyclerAdapter(List<PhotoInfo> mData) {
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
            if (recycleItemClickListener != null) {
                recycleItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.fileIconView.setImageBitmap(mData.get(position).getFileIcon());
        holder.fileNameView.setText(mData.get(position).getFileName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}

