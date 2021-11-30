package com.asteriskmanager.fragments.channelfragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
        channelViewHolder.channelName.setText(ChannelList.get(i).getChannelName());
        channelViewHolder.callerId.setText(ChannelList.get(i).getCallerIDNum());
        channelViewHolder.callerLineNum.setText(ChannelList.get(i).getConnectedLineNum());
        channelViewHolder.callerDuration.setText(ChannelList.get(i).getDuration());
        if(ChannelList.get(i).isActive()){
            channelViewHolder.channelLayout.setBackgroundColor(Color.GRAY);
        }
        channelViewHolder.channelLayout.setOnClickListener(v -> mListener.onChannelClick(i));
    }

    @Override
    public int getItemCount() {
        return ChannelList.size();
    }

    static class ChannelViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView channelName;
        TextView callerId;
        TextView callerLineNum;
        TextView callerDuration;
        LinearLayout channelLayout;

        @SuppressLint("CutPasteId")
        ChannelViewHolder(View itemView)  {
            super(itemView);
            channelName = itemView.findViewById(R.id.ChannelName);
            callerId = itemView.findViewById(R.id.CallerIDNum);
            callerLineNum = itemView.findViewById(R.id.ConnectedLineNum);
            callerDuration = itemView.findViewById(R.id.Duration);
            channelLayout = itemView.findViewById(R.id.ChannelRecordLayout);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListener.onChannelClick(position);
        }
    }
}
