package com.openclassroom.go4lunch.ui.drawerMenu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.messaging.FirebaseMessaging;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.utils.ConstantString;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class SettingFragment extends Fragment {

    @SuppressWarnings("ConstantConditions")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ){
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        Switch switchNotification = view.findViewById(R.id.sw_enable_disable_notification);
        if(getActivity().getSharedPreferences(ConstantString.NOTIFICATION_ENABLE, Activity.MODE_PRIVATE).getBoolean(ConstantString.EATING, false)){
            switchNotification.setChecked(true);
        }else{
            switchNotification.setChecked(false);
        }

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) disableNotification();
            else enableNotification();
        });

        return view;
    }

    @SuppressWarnings("ConstantConditions")
    private void enableNotification(){
        FirebaseMessaging.getInstance().subscribeToTopic(ConstantString.TOPIG_TO_SUBSCRIBE);
        SharedPreferences pref = getActivity().getSharedPreferences(ConstantString.NOTIFICATION_ENABLE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(ConstantString.EATING, true);
        editor.apply();
        Toast.makeText(getActivity(), R.string.notification_enable, Toast.LENGTH_SHORT).show();
    }


    @SuppressWarnings("ConstantConditions")
    private void disableNotification(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(ConstantString.TOPIG_TO_SUBSCRIBE);
        SharedPreferences pref = getActivity().getSharedPreferences(ConstantString.NOTIFICATION_ENABLE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(ConstantString.EATING, false);
        editor.apply();
        Toast.makeText(getActivity(), R.string.notification_disable, Toast.LENGTH_SHORT).show();
    }
}
