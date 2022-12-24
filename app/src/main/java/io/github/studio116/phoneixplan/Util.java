package io.github.studio116.phoneixplan;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.util.Date;

public class Util {
    /**
     * Write data to a file
     */
    public static void write(Context appContext, String filename, String content) {
        File path = appContext.getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, filename));
            writer.write(content.getBytes());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read data from a file
     */
    public static String read(Context appContext, String filename) {
        File path = appContext.getFilesDir();
        File file = new File(path, filename);
        if (!file.exists()) {
            return null;
        }
        StringBuilder data = new StringBuilder();
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String readString = bufferedReader.readLine();
            while (readString != null) {
                data.append(readString);
                readString = bufferedReader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data.toString();
    }

    /**
     * Check if two dates reference the same day
     */
    public static boolean isNotSameDay(Date a, Date b) {
        // https://www.baeldung.com/java-check-two-dates-on-same-day#1-using-localdate
        return !a.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(b.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
}
