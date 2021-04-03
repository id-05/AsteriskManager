package com.asteriskmanager;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ConfigFileAdapter extends RecyclerView.Adapter<ConfigFileAdapter.ViewHolder>{
    View view;
    ViewHolder viewHolder;
    ArrayList<ConfigFileRecord> filesList;
    private static OnRecordClickListener mListener;

    interface OnRecordClickListener {
        void onRecordClick(int position);
    }

    public void setOnRecordClickListener(OnRecordClickListener listener) {
        mListener = listener;
    }

    public ConfigFileAdapter(ArrayList<ConfigFileRecord> filesList){
        this.filesList = filesList;
    }

    @Override
    public ConfigFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.configfiles_recyclerview,parent,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        ConfigFileRecord bufRecord = filesList.get(position);
        holder.filename.setText(bufRecord.getFilename());
        holder.description.setText(bufRecord.getDescription());
        if(bufRecord.getCategory()!=null){
            holder.category.setText("\n\r"+"\n\r"+bufRecord.getCategory());
        }else {
            holder.category.setText("");
        }
        holder.recordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRecordClick(position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return filesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView filename;
        public TextView description;
        public TextView category;
        public LinearLayout recordLayout;

        public ViewHolder(View v){
            super(v);
            filename = v.findViewById(R.id.filename);
            description = v.findViewById(R.id.description);
            category = v.findViewById(R.id.category);
            recordLayout = v.findViewById(R.id.recordLayout);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListener.onRecordClick(position);
        }
    }
}
