package com.asteriskmanager;

//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by JUNED on 6/10/2016.
 */

public class ConfigFileAdapter extends RecyclerView.Adapter<ConfigFileAdapter.ViewHolder>{
    View view;
    ViewHolder viewHolder;
    ArrayList<ConfigFileRecord> filesList = new ArrayList<>();

    public ConfigFileAdapter(ArrayList<ConfigFileRecord> filesList){
        this.filesList = filesList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView filename;
        public TextView description;
        public TextView category;

        public ViewHolder(View v){
            super(v);
            filename = v.findViewById(R.id.filename);
            description = v.findViewById(R.id.description);
            category = v.findViewById(R.id.category);
        }
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
            holder.category.setText(bufRecord.getCategory());
          //  holder.category.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        }else {
            holder.category.setText("");
         //   holder.category.setHeight(0);
        }
    }

    @Override
    public int getItemCount(){
        return filesList.size();
    }
}
