package io.github.studio116.phoenixplan;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.ListPreference;
import androidx.preference.ListPreferenceDialogFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import io.github.studio116.phoenixplan.databinding.ActivitySettingsBinding;
import io.github.studio116.phoenixplan.notification.Scheduler;

public class SettingsActivity extends BaseActivity implements PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appbarLayout.toolbar);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference rescheduleNotifications = findPreference("reschedule_notifications");
            assert rescheduleNotifications != null;
            rescheduleNotifications.setOnPreferenceClickListener(preference -> {
                Scheduler.update(requireContext());
                return true;
            });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("dark_mode")) {
                SubApplication.setDarkMode(getContext());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onDisplayPreferenceDialog(@NonNull Preference preference) {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    // https://stackoverflow.com/a/74112704/16198887
    public static class MaterialListPreference extends ListPreferenceDialogFragmentCompat {
        public MaterialListPreference(Preference preference) {
            Bundle bundle = new Bundle(1);
            bundle.putString(ARG_KEY, preference.getKey());
            setArguments(bundle);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(getPreference().getTitle())
                    .setPositiveButton(getPreference().getPositiveButtonText(), this)
                    .setNegativeButton(getPreference().getNegativeButtonText(), this);
            View root = onCreateDialogView(requireActivity());
            if (root == null) {
                builder.setMessage(getPreference().getDialogMessage());
            } else {
                onBindDialogView(root);
                builder.setView(root);
            }
            onPrepareDialogBuilder(builder);
            return builder.create();
        }
    }

    @Override
    public boolean onPreferenceDisplayDialog(@NonNull PreferenceFragmentCompat caller, @NonNull Preference pref) {
        if (pref instanceof ListPreference) {
            MaterialListPreference dialogFragment = new MaterialListPreference(pref);
            //noinspection deprecation
            dialogFragment.setTargetFragment(caller, 0);
            dialogFragment.show(getSupportFragmentManager(), dialogFragment.toString());
            return true;
        } else {
            return false;
        }
    }
}