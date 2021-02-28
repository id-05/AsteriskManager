package com.asteriskmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
//import static com.id05.asteriskcallmedisa.MainActivity.*;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.AsteriskManagerViewHolder>  {

    private ArrayList<AsteriskServer> ServerList = new ArrayList<>();
    private final Context context;

    interface OnRecordClickListener {
        void onRecordClick(int position);
    }

    private static OnRecordClickListener mListener;


    RecordAdapter(ArrayList<AsteriskServer> ServerList, Context context){
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

        asteriskManagerViewHolder.serverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // asteriskManagerViewHolder.serverLayout.setBackgroundColor(Color.GRAY);
                notifyItemChanged(i);
                

            }
        });

        asteriskManagerViewHolder.menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, asteriskManagerViewHolder.menuicon);
                popupMenu.inflate(R.menu.recycler_adapter_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
        ImageView menuicon;

        AsteriskManagerViewHolder(View itemView)  {
            super(itemView);
            serverName = itemView.findViewById(R.id.nameServer);
            serverInfo = itemView.findViewById(R.id.infoServer);
            serverLayout = itemView.findViewById(R.id.recordLayout);
            menuicon = itemView.findViewById(R.id.recordMenu);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListener.onRecordClick(position);
        }
    }
}
