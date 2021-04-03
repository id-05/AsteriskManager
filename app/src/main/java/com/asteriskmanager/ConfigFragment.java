package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import java.util.ArrayList;

public class ConfigFragment extends Fragment implements ConfigFileAdapter.OnRecordClickListener {

    RecyclerView recyclerView;
    AsteriskServer currentServer;
    AmiState amiState = new AmiState();
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
        amiState.setAction("open");
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
        ConfigFileAdapter recyclerViewAdapter = new ConfigFileAdapter(filesList);
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
        EditConfigFileFragment fragment = new EditConfigFileFragment();
        fragment.setArguments(bundle);
        AsteriskServerActivity.fragmentTransaction.replace(R.id.container, fragment);
        AsteriskServerActivity.fragmentTransaction.commit();
        AsteriskServerActivity.configfileactive = true;
    }
}