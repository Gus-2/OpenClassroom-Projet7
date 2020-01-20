package com.openclassroom.go4lunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class SettingFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ){
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }
}
