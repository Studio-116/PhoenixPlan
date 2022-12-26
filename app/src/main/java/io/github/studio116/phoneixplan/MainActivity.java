package io.github.studio116.phoneixplan;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.github.studio116.phoneixplan.dialog.EditTimelineObjectDialog;
import io.github.studio116.phoneixplan.model.Timeline;
import io.github.studio116.phoneixplan.databinding.ActivityMainBinding;
import io.github.studio116.phoneixplan.recyclerview.TimelineAdapter;

public class MainActivity extends AppCompatActivity {
    private TimelineAdapter adapter = null;
    private final Timeline timeline = new Timeline() {
        @Override
        public void save(Context context) {
            super.save(context);
            if (adapter != null && adapter.timeline == this) {
                adapter.rebuild(getResources());
            }
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
        adapter = new TimelineAdapter(timeline, getResources());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup FAB
        binding.fab.setOnClickListener(view -> new EditTimelineObjectDialog(MainActivity.this, timeline, -1));
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
            // TODO: Make a settings activity.
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}