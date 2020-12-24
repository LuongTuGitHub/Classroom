package com.application.project.classroom.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import com.application.project.classroom.R;
import com.application.project.classroom.activity.SetImageUserActivity;


@SuppressLint("ValidFragment")
public
class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.xml_preference);
        Preference preference = getPreferenceManager().findPreference(getResources().getString(R.string.change_image_user));
        Intent intent = new Intent(getActivity(), SetImageUserActivity.class);
        preference.setIntent(intent);

    }

}
