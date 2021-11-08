package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ManagerRecordAdapter extends RecyclerView.Adapter<ManagerRecordAdapter.ManagerViewHolder>  {

    private ArrayList<ManagerRecord> ManagerList;
    private static OnManagerClickListener mListener;

    interface OnManagerClickListener {
        void onManagerClick(int position);
    }

    public void setOnManagerClickListener(ManagerRecordAdapter.OnManagerClickListener listener) {
        mListener = listener;
    }

    ManagerRecordAdapter(ArrayList<ManagerRecord> ManagerList){
        this.ManagerList = ManagerList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_record_layout, parent, false);
        return new ManagerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManagerViewHolder managerViewHolder, final int i) {
        managerViewHolder.ManagerName.setText(ManagerList.get(i).getName());
        managerViewHolder.ManagerDeny.setText(ManagerList.get(i).getDeny());
        managerViewHolder.ManagerPermit.setText(ManagerList.get(i).getPermit());

        managerViewHolder.managerLayout.setOnClickListener(v -> mListener.onManagerClick(i));
    }

    @Override
    public int getItemCount() {
        return ManagerList.size();
    }

    static class ManagerViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView ManagerName;
        TextView ManagerDeny;
        TextView ManagerPermit;
        LinearLayout managerLayout;

        @SuppressLint("CutPasteId")
        ManagerViewHolder(View itemView)  {
            super(itemView);
            ManagerName = itemView.findViewById(R.id.ManagerName);
            ManagerDeny = itemView.findViewById(R.id.ManagerDeny);
            ManagerPermit = itemView.findViewById(R.id.ManagerPermit);
            managerLayout = itemView.findViewById(R.id.ManagerRecordLayout);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListener.onManagerClick(position);
        }
    }
}