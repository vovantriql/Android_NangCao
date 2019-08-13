package vuthanhtutrang.com.flagsquiz;

import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SettingsMainActivityFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
