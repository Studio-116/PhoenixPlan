package io.github.studio116.phoenixplan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.studio116.phoenixplan.dialog.EditTimelineObjectDialog;
import io.github.studio116.phoenixplan.dialog.ViewTimelineObjectDialog;
import io.github.studio116.phoenixplan.model.Timeline;
import io.github.studio116.phoenixplan.databinding.ActivityMainBinding;
import io.github.studio116.phoenixplan.recyclerview.TimelineAdapter;

public class MainActivity extends BaseActivity {
    public static final String ARGUMENT_EXTRA = "io.github.studio116.phoneixplan.ARGUMENT_EXTRA";
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});
    private TimelineAdapter adapter = null;
    private final Timeline timeline = new Timeline() {
        @Override
        public void save(Context context) {
            super.save(context);
            if (adapter != null && adapter.timeline == this) {
                adapter.rebuild(context);
            }
        }
    };

    private final Handler handler = new Handler();
    private static final int REFRESH_INTERVAL = 5000; // Milliseconds
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            refresh();
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Timeline
        timeline.load(getApplicationContext());

        // Load UI
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appbarLayout.toolbar);

        // Setup Timeline
        adapter = new TimelineAdapter(timeline, this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup FAB
        binding.fab.setOnClickListener(view -> new EditTimelineObjectDialog(MainActivity.this, timeline, null));

        // Request Notification Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Handle Argument
        if (getIntent().hasExtra(ARGUMENT_EXTRA) && savedInstanceState == null) {
            UUID id = (UUID) getIntent().getSerializableExtra(ARGUMENT_EXTRA);
            new ViewTimelineObjectDialog(this, timeline, id);
        }
    }

    public List<DialogFragment> dialogsToDismiss = new ArrayList<>();

    @Override
    protected void onPause() {
        super.onPause();
        // Cancel Refreshing
        handler.removeCallbacks(refreshRunnable);
        // Dismiss Dialogs
        for (DialogFragment dialog : dialogsToDismiss) {
            dialog.dismissAllowingStateLoss();
        }
        dialogsToDismiss.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh
        refresh();
        // Schedule Refreshing
        handler.post(refreshRunnable);
    }

    private void refresh() {
        if (adapter != null) {
            adapter.rebuild(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}