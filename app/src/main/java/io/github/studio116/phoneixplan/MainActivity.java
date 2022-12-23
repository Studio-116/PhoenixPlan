package io.github.studio116.phoneixplan;

import static io.github.studio116.phoneixplan.Model.ModelTypeAdapter.read;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.studio116.phoneixplan.Model.EventTimeline;
import io.github.studio116.phoneixplan.Model.Timeline;
import io.github.studio116.phoneixplan.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private Timeline myTimeline;

    /**
     * Init timeline from in "timeline.json" file
     * */

    private void setMyTimeline() {
        try {
            myTimeline = new Timeline(read(getApplicationContext(), "timeline.json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Example timeline save code
     * */

    private void addEventsToTimeline() {
        EventTimeline Event1 = new EventTimeline();
        Event1.DATE = new Date();
        Event1.DESCRIPTION = "Testing 101";

        EventTimeline Event2 = new EventTimeline();
        Event2.DATE = new Date();
        Event2.DESCRIPTION = "Testing 202";

        try {
            myTimeline.createEvent("Test1", Event1);
            myTimeline.createEvent("Test2", Event2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        myTimeline.SaveToFile(getApplicationContext());
    }

    /**
     * Example timeline read code
     * */

    private void getMyTimeline() {
        Log.d("current saved timeline",read(getApplicationContext(), "timeline.json"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMyTimeline();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        getMyTimeline();
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