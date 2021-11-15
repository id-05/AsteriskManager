package com.asteriskmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import static com.asteriskmanager.MainActivity.print;

public class ManagerFragmentEditor extends Fragment {

    TextView mName, mSecret, mDeny, mPermit, mTimeout, mRead, mWrite;
    ManagerRecord record;

    public ManagerFragmentEditor() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        setHasOptionsMenu(true);
        if(arguments!=null){
            Log.d("asteriskmanager","name from managerlist "+ManagerFragment.ManagerList.get(arguments.getInt("filename")).getName());
            record = ManagerFragment.ManagerList.get(arguments.getInt("filename"));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manager_editor_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_manager) {
            //save change
            Log.d("asteriskmanager","save manager");
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        mRead = fragmentView.findViewById(R.id.managerReadEdit);
        mRead.setText(record.getRead());
        mWrite = fragmentView.findViewById(R.id.managerWriteEdit);
        mWrite.setText(record.getWrite());
        mWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose an device:");
                builder.setNegativeButton("Cancel",null);
                int i = 13;
                String[] rulesName = {"system","call","log","verbose","command","agent","user","config","dtmf","reporting","cdr","dialplan","originate"};
                boolean[] checkedItems = new boolean[i];
                for(int j = 0; j < i; j++){
                    checkedItems[j] = mWrite.getText().toString().contains(rulesName[j]);
                }

                builder.setMultiChoiceItems(rulesName, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                });

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder bufStr = new StringBuilder();
                        for(int j = 0; j < i; j++){
                            if(checkedItems[j]){
                                bufStr.append(",").append(rulesName[j]);
                            }else{
                                checkedItems[j] = false;
                            }
                        }
                        String cutBuf = bufStr.toString().substring(1,bufStr.toString().length()-1);
                        Log.d("asteriskmanager", cutBuf);
                        mWrite.setText(cutBuf);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        mTimeout = fragmentView.findViewById(R.id.managerTimeoutEdit);
        mTimeout.setText(record.getTimeout());
        return fragmentView;
    }
}