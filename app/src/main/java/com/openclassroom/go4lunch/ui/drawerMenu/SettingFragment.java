package com.openclassroom.go4lunch.ui.drawerMenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.messaging.FirebaseMessaging;
import com.openclassroom.go4lunch.R;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class SettingFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ){
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        Switch switchNotification = view.findViewById(R.id.sw_enable_disable_notification);
        if(getActivity().getSharedPreferences("NotificationEnable", getActivity().MODE_PRIVATE).getBoolean("Eating", false)){
            switchNotification.setChecked(true);
        }else{
            switchNotification.setChecked(false);
        }

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) disableNotification();
                else enableNotification();
            }
        });

        return view;
    }

    public void enableNotification(){
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        SharedPreferences pref = getActivity().getSharedPreferences("NotificationEnable", getActivity().MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("Eating", true);
        editor.apply();
        Toast.makeText(getActivity(), "Notification enabled !", Toast.LENGTH_SHORT).show();
    }

    public void disableNotification(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all");
        SharedPreferences pref = getActivity().getSharedPreferences("NotificationEnable", getActivity().MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("Eating", false);
        editor.apply();
        Toast.makeText(getActivity(), "Notification disable !", Toast.LENGTH_SHORT).show();
    }
}
