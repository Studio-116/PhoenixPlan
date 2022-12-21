package io.github.studio116.phoneixplan.Model;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModelTypeAdapter {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Converts string date format to Date class format
     * @param strDate
     * */
    public static Date StringToDate(String strDate) {
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Converts Date class format to string date format
     * @param newDate
     * */
    public static String DateToString(Date newDate) {
        return dateFormat.format(newDate);
    }

    /**
     * Write your raw JSONObject to a file
     * @param appContext
     * @param filename
     * @param content
     * */

    public static void write(Context appContext, String filename, JSONObject content) {
        File path = appContext.getFilesDir();

        try {
            FileOutputStream writer = new FileOutputStream(new File(path, filename));
            writer.write(content.toString().getBytes());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads your JSON file
     * @param appContext
     * @param filename
     * */

    public static String read(Context appContext, String filename) {
        File path = appContext.getFilesDir();
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream reader = new FileInputStream(new File(path, filename));
            InputStreamReader isr = new InputStreamReader( reader ) ;
            BufferedReader buffreader = new BufferedReader( isr ) ;

            String readString = buffreader.readLine ( ) ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( ) ;
            }

            isr.close ( ) ;
        } catch ( IOException ioe ) {
            ioe.printStackTrace ( ) ;
        }
        return datax.toString();
    }
}
