package com.asteriskmanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.asteriskmanager.MainActivity.print;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditConfigFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditConfigFileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String filename;

    public EditConfigFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditConfigFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditConfigFileFragment newInstance(String param1, String param2) {
        EditConfigFileFragment fragment = new EditConfigFileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filename = getArguments().getString("filename");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_edit_config_file, container, false);
        return fragmentView;
    }


}