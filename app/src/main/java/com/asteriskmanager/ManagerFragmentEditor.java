package com.asteriskmanager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ManagerFragmentEditor extends Fragment {

    TextView mName, mSecret, mDeny, mPermit, mTimeout;
    ManagerRecord record;

    public ManagerFragmentEditor() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments!=null){
            Log.d("asteriskmanager","name from managerlist "+ManagerFragment.ManagerList.get(arguments.getInt("filename")).getName());
            record = ManagerFragment.ManagerList.get(arguments.getInt("filename"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_manager_editor, container, false);
        mName = fragmentView.findViewById(R.id.managerNameEdit);
        mName.setText(record.getName());
        mSecret = fragmentView.findViewById(R.id.managerSecretEdit);
        mSecret.setText(record.getSecret());
        mDeny = fragmentView.findViewById(R.id.managerDenyEdit);
        mDeny.setText(record.getDeny());
        mPermit = fragmentView.findViewById(R.id.managerPermitEdit);
        mPermit.setText(record.getPermit());
        mTimeout = fragmentView.findViewById(R.id.managerTimeoutEdit);
        mTimeout.setText(record.getTimeout());
        return fragmentView;
    }
}