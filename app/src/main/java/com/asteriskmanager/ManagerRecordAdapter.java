package com.asteriskmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
//import static com.id05.asteriskcallmedisa.MainActivity.*;

public class ManagerRecordAdapter extends RecyclerView.Adapter<ManagerRecordAdapter.ManagerViewHolder>  {

    private ArrayList<ManagerRecord> ManagerList = new ArrayList<>();
    //private final Context context;

    interface OnRecordClickListener {
        void onRecordClick(int position);
    }

    private static OnRecordClickListener mListener;


    ManagerRecordAdapter(ArrayList<ManagerRecord> ManagerList){
        this.ManagerList = ManagerList;
       // this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_recyclerview_layout, parent, false);
        return new ManagerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManagerViewHolder managerViewHolder, final int i) {
        managerViewHolder.serverName.setText(ManagerList.get(i).getName());
      //  managerViewHolder.serverInfo.setText(ManagerList.get(i).getIpaddress());



    }

    public void setOnRecordClickListener(OnRecordClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return ManagerList.size();
    }

    static class ManagerViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView serverName;
        TextView serverInfo;
        LinearLayout serverLayout;
        ImageView menuicon,connectIcon;

        ManagerViewHolder(View itemView)  {
            super(itemView);
            serverName = itemView.findViewById(R.id.nameServer);
            serverInfo = itemView.findViewById(R.id.infoServer);
            serverLayout = itemView.findViewById(R.id.recordLayout);
            menuicon = itemView.findViewById(R.id.recordMenu);
            connectIcon = itemView.findViewById(R.id.connectIcon);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListener.onRecordClick(position);
        }
    }
}