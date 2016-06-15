package nl.geekk.taskmanager.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;

import nl.geekk.taskmanager.R;

public class PreferencesFragment extends PreferenceFragmentCompat {
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        mainActivity = (MainActivity) getActivity();

        addPreferencesFromResource(R.xml.user_defined_preferences);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_defined_preferences, false);
    }
}