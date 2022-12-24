package io.github.studio116.phoneixplan;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

import io.github.studio116.phoneixplan.model.DeadlineObject;
import io.github.studio116.phoneixplan.model.EventObject;
import io.github.studio116.phoneixplan.model.Timeline;
import io.github.studio116.phoneixplan.databinding.ActivityMainBinding;
import io.github.studio116.phoneixplan.model.TimelineObject;
import io.github.studio116.phoneixplan.recyclerview.TimelineAdapter;

public class MainActivity extends AppCompatActivity {
    private final Timeline timeline = new Timeline();

    /**
     * Example timeline save code
     */
    private void createTestTimeline() {
        timeline.objects.clear();
        EventObject event1 = new EventObject();
        event1.name = "Event 1";
        event1.description = "Testing testing";
        event1.importance = TimelineObject.Importance.VERY_HIGH;
        event1.timeFrom = new Date();
        event1.timeTo = new Date();
        timeline.objects.add(event1);
        DeadlineObject deadline1 = new DeadlineObject();
        deadline1.name = "Deadline 1";
        deadline1.description = "Testing testing";
        deadline1.importance = TimelineObject.Importance.LOW;
        deadline1.time = new Date();
        timeline.objects.add(deadline1);
        EventObject event2 = new EventObject();
        event2.name = "Multi-Day Event";
        event2.description = "Testing testing";
        event2.importance = TimelineObject.Importance.NORMAL;
        event2.timeFrom = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 5);
        event2.timeTo = calendar.getTime();
        timeline.objects.add(event2);
        // Save
        timeline.save(getApplicationContext());
    }

    /**
     * Example timeline read code
     */
    private void debugTimeline() {
        Log.d("current saved timeline", Util.read(getApplicationContext(), Timeline.FILE_NAME));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Timeline
        timeline.load(getApplicationContext());

        // Testing
        createTestTimeline();

        // Load UI
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Setup Timeline
        binding.recyclerView.setAdapter(new TimelineAdapter(timeline));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup FAB
        binding.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        debugTimeline();
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