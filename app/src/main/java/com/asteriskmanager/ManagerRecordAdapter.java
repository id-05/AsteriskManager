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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_record_layout, parent, false);
        return new ManagerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManagerViewHolder managerViewHolder, final int i) {
        managerViewHolder.ManagerName.setText(ManagerList.get(i).getName());
        managerViewHolder.ManagerDeny.setText(ManagerList.get(i).getDeny());
        managerViewHolder.ManagerPermit.setText(ManagerList.get(i).getPermit());
    }

    public void setOnRecordClickListener(OnRecordClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return ManagerList.size();
    }

    static class ManagerViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView ManagerName;
        TextView ManagerDeny;
        TextView ManagerPermit;
        LinearLayout serverLayout;

        @SuppressLint("CutPasteId")
        ManagerViewHolder(View itemView)  {
            super(itemView);
            ManagerName = itemView.findViewById(R.id.ManagerName);
            ManagerDeny = itemView.findViewById(R.id.ManagerDeny);
            ManagerPermit = itemView.findViewById(R.id.ManagerPermit);
            serverLayout = itemView.findViewById(R.id.ManagerRecordLayout);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListener.onRecordClick(position);
        }
    }
}