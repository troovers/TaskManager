package nl.geekk.taskmanager.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.preference.PreferenceFragmentCompat;

import nl.geekk.taskmanager.R;

public class PreferencesFragment extends PreferenceFragmentCompat {
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        mainActivity = (MainActivity) getActivity();

        addPreferencesFromResource(R.xml.main_login_preferences);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.main_login_preferences, false);
    }
}