package com.asteriskmanager.configfragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asteriskmanager.AsteriskServer;
import com.asteriskmanager.AsteriskServerActivity;
import com.asteriskmanager.ConfigFileRecord;
import com.asteriskmanager.ConfigFragmentEditor;
import com.asteriskmanager.R;

import java.util.ArrayList;

public class ConfigFragment extends Fragment implements ConfigFragmentAdapter.OnRecordClickListener {

    RecyclerView recyclerView;
    AsteriskServer currentServer;
    ArrayList<ConfigFileRecord> filesList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentServer = AsteriskServerActivity.Server;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_config, container, false);
        recyclerView = fragmentView.findViewById(R.id.RecyclerConfigFiles);
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        String[] filesname = getResources().getStringArray(R.array.ConfigFile);
        String[] filesdescription = getResources().getStringArray(R.array.ConfigFileDescription);
        filesList.clear();
        for(int i=0; i<filesname.length;i++){
            ConfigFileRecord BufRecord = new ConfigFileRecord();
            BufRecord.setFilename(filesname[i]);
            BufRecord.setDescription(filesdescription[i]);
            switch (filesname[i]){
                case "adtranvofr.conf":{BufRecord.setCategory("Channels");break;}
                case "adsi.conf":{BufRecord.setCategory("Analog Display Services Interface");break;}
                case "extensions.conf":{BufRecord.setCategory("Extensions");break;}
                case "alarmreceiver.conf":{BufRecord.setCategory("Comands Extensions");break;}
                case "amd.conf":{BufRecord.setCategory("Others");break;}
            }
            filesList.add(BufRecord);
        }
        ConfigFragmentAdapter recyclerViewAdapter = new ConfigFragmentAdapter(filesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnRecordClickListener(this);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRecordClick(int position) {
        getActivity().setTitle(getActivity().getTitle()+" / "+filesList.get(position).filename);
        AsteriskServerActivity.fragmentTransaction = AsteriskServerActivity.fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("filename", filesList.get(position).filename);
        ConfigFragmentEditor fragment = new ConfigFragmentEditor();
        fragment.setArguments(bundle);
        AsteriskServerActivity.fragmentTransaction.replace(R.id.container, fragment);
        AsteriskServerActivity.fragmentTransaction.commit();
        AsteriskServerActivity.subFragment = "config";
    }
}