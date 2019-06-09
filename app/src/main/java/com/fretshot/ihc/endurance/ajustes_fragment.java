package com.fretshot.ihc.endurance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;


public class ajustes_fragment extends PreferenceFragmentCompat {

    public static String IP_ENDURANCE;

    public ajustes_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        PreferenceManager.setDefaultValues(getActivity().getApplicationContext(), R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        IP_ENDURANCE = sharedPreferences.getString(ajustes.IP_ENDURANCE,null);

        Snackbar.make(getActivity().findViewById(android.R.id.content),"IP Endurance: "+IP_ENDURANCE, Snackbar.LENGTH_INDEFINITE).show();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        android.support.v4.app.NavUtils.navigateUpFromSameTask(getActivity());
    }
}