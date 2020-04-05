package com.example.iat359_finalapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by helmine on 2015-02-04.
 */
public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    public MyDatabase(Context c) {
        context = c;
        helper = new MyHelper(context);
    }

    public long insertData(String name, String type, String distance, String output, String volume, String vibrate) {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.NAME, name);
        contentValues.put(Constants.TYPE, type);
        contentValues.put(Constants.DISTANCE, distance);
        contentValues.put(Constants.OUTPUT, output);
        contentValues.put(Constants.VOLUME, volume);
        contentValues.put(Constants.VIBRATE, vibrate);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData() //get all data button related
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.RID, Constants.NAME, Constants.TYPE, Constants.DISTANCE, Constants.OUTPUT, Constants.VOLUME, Constants.VIBRATE};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }


    public String getSelectedData(String type) {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.NAME, Constants.TYPE};

        String selection = Constants.TYPE + "='" + type + "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(Constants.NAME);
            int index2 = cursor.getColumnIndex(Constants.TYPE);
            int index3 = cursor.getColumnIndex(Constants.DISTANCE);
            int index4 = cursor.getColumnIndex(Constants.OUTPUT);
            int index5 = cursor.getColumnIndex(Constants.VOLUME);
            int index6 = cursor.getColumnIndex(Constants.VIBRATE);
            String routeName = cursor.getString(index1);
            String routeType = cursor.getString(index2);
            int routeDist = cursor.getInt(index3);
            String routeOutput = cursor.getString(index4);
            int routeVolume = cursor.getInt(index5);
            String routeVibrate = cursor.getString(index6);
            buffer.append(routeName + " " + routeType + " " + routeDist + routeOutput + routeVolume + routeVibrate + "\n");
        }
        return buffer.toString();
    }

    public int deleteRow() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = {"route"};
        int count = db.delete(Constants.TABLE_NAME, Constants.TYPE + "=?", whereArgs);
        return count;
    }


}
