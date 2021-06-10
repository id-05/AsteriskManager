package com.asteriskmanager;

import android.annotation.SuppressLint;
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

public class ServerRecordAdapter extends RecyclerView.Adapter<ServerRecordAdapter.AsteriskManagerViewHolder>  {

    private ArrayList<AsteriskServer> ServerList;
    private final Context context;

    interface OnRecordClickListener {
        void onRecordClick(int position);
    }

    private static OnRecordClickListener mListener;


    ServerRecordAdapter(ArrayList<AsteriskServer> ServerList, Context context){
        this.ServerList = ServerList;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public AsteriskManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_recyclerview_layout, parent, false);
        return new AsteriskManagerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AsteriskManagerViewHolder asteriskManagerViewHolder, final int i) {
        asteriskManagerViewHolder.serverName.setText(ServerList.get(i).getName());
        asteriskManagerViewHolder.serverInfo.setText(ServerList.get(i).getIpaddress());


        if(ServerList.get(i).getOnline()) {
            asteriskManagerViewHolder.connectIcon.setImageResource(R.drawable.ic_baseline_link_24);

        }else{
            asteriskManagerViewHolder.connectIcon.setImageResource(R.drawable.ic_baseline_link_off_24);
        }

        asteriskManagerViewHolder.serverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRecordClick(i);
                notifyItemChanged(i);
            }
        });

        asteriskManagerViewHolder.menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, asteriskManagerViewHolder.menuicon);
                popupMenu.inflate(R.menu.recycler_adapter_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.itemDelete:
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setTitle(R.string.confirmdeletion);
                                alertDialog.setMessage(R.string.areyousure);
                                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
                                        MainActivity.serverDelBase(ServerList.get(i));
                                        ServerList.remove(i);
                                        notifyDataSetChanged();
                                    }
                                });

                                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertDialog.show();
                            }
                            break;
                            case R.id.itemConSettings:
                            {
                                Intent intent = new Intent(context,AddNewServer.class);
                                intent.putExtra("method","edit");
                                intent.putExtra("serverid",ServerList.get(i).getId());
                                context.startActivity(intent);
                            }
                            break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void setOnRecordClickListener(OnRecordClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return ServerList.size();
    }

    static class AsteriskManagerViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView serverName;
        TextView serverInfo;
        LinearLayout serverLayout;
        ImageView menuicon,connectIcon;

        AsteriskManagerViewHolder(View itemView)  {
            super(itemView);
            serverName = itemView.findViewById(R.id.ManagerName);
            serverInfo = itemView.findViewById(R.id.ManagerPermit);
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
