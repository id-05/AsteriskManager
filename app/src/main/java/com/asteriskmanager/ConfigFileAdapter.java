package com.asteriskmanager;

//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by JUNED on 6/10/2016.
 */

public class ConfigFileAdapter extends RecyclerView.Adapter<ConfigFileAdapter.ViewHolder>{

    String[] SubjectValues;
    //Context context;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;

    public ConfigFileAdapter(String[] SubjectValues1){

        this.SubjectValues = SubjectValues1;
        //context = context1;
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
        view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.configfiles_recyclerview,parent,false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        holder.filename.setText(SubjectValues[position]);
        holder.description.setText("description of file this include your description of file from string list");
        holder.category.setHeight(0);
    }

    @Override
    public int getItemCount(){

        return SubjectValues.length;
    }
}
