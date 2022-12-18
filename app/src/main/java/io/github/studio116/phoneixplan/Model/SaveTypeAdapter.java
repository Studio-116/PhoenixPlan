package io.github.studio116.phoneixplan.Model;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveTypeAdapter {
    /**
     * Write your raw JSONObject to a file
     * @param appContext
     * @param filename
     * @param content
     * */

    public static void write(Context appContext, String filename, JSONObject content) {
        File path = appContext.getFilesDir();
       Toast.makeText(appContext,appContext.getFilesDir().getAbsolutePath(), Toast.LENGTH_LONG);
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, filename));
            writer.write(content.toString().getBytes());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
