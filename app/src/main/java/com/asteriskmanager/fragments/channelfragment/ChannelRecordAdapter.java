package com.asteriskmanager.fragments.channelfragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.asteriskmanager.R;

import java.util.ArrayList;

public class ChannelRecordAdapter extends RecyclerView.Adapter<ChannelRecordAdapter.ChannelViewHolder>  {

    public ArrayList<ChannelRecord> ChannelList;
    private static ChannelRecordAdapter.OnChannelClickListener mListener;

    interface OnChannelClickListener {
        void onChannelClick(int position);
    }

    public void setOnChannelClickListener(ChannelRecordAdapter.OnChannelClickListener listener) {
        mListener = listener;
    }

    ChannelRecordAdapter(ArrayList<ChannelRecord> ChannelList){
        this.ChannelList = ChannelList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ChannelRecordAdapter.ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_record_layout, parent, false);
        return new ChannelRecordAdapter.ChannelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChannelRecordAdapter.ChannelViewHolder channelViewHolder, final int i) {
        channelViewHolder.ChannelName.setText(ChannelList.get(i).getChannelName());

        channelViewHolder.channelLayout.setOnClickListener(v -> mListener.onChannelClick(i));
    }

    @Override
    public int getItemCount() {
        return ChannelList.size();
    }

    static class ChannelViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView ChannelName;
        LinearLayout channelLayout;

        @SuppressLint("CutPasteId")
        ChannelViewHolder(View itemView)  {
            super(itemView);
            ChannelName = itemView.findViewById(R.id.ChannelName);
            channelLayout = itemView.findViewById(R.id.ChannelRecordLayout);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //mListener.onManagerClick(position);
        }
    }
}
