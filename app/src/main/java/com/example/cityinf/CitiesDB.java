package com.example.cityinf;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

public class CitiesDB extends ContentProvider {

    public static final String COUNTRY = "country";
    public static final String CITY = "city";

    private static final String DISTINCT = "distinct";
    private final int DB_VERSION = 1;
    private static final int URI_LIST = 1;
    private static final int URI_DISTINCT = 2;
    private final String DB_NAME = "countries_data";
    private static final String TABLE_NAME = "cities_data";
    private static final String AUTHORITY = "citiesDB";

    private final String DB_TABLE_CREATE = " CREATE TABLE " + TABLE_NAME + " ( " +
            COUNTRY + " TEXT NOT NULL , " +
            CITY + " TEXT NOT NULL ) ; ";

    public static final Uri simpleUri = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    public static final Uri uriCountries = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + DISTINCT);

    private static DataBaseHelper dataBaseHelper;
    public static SQLiteDatabase sqLiteDatabase;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TABLE_NAME, URI_LIST);
        uriMatcher.addURI(AUTHORITY, TABLE_NAME + DISTINCT, URI_DISTINCT);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        sqLiteDatabase.insert(TABLE_NAME, null, values);

        return uri;
    }

    @Override
    public boolean onCreate() {
        dataBaseHelper = new DataBaseHelper(getContext());
        return dataBaseHelper == null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();

        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case URI_LIST:
                cursor = sqLiteDatabase.query(TABLE_NAME,projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case URI_DISTINCT:
                cursor = sqLiteDatabase.query(true, TABLE_NAME, new String[]{COUNTRY}, selection, selectionArgs,
                        null, null, null, sortOrder);
                break;
            default:
                cursor = null;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static void fastInsert(JSONObject jsonObject) {
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        JSONArray data;
        JSONArray cities = jsonObject.names();

        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < cities.length(); i++) {
                data = jsonObject.getJSONArray(cities.getString(i));
                int size = data.length();
                if (cities.getString(i).equals(""))
                    continue;
                for (int j = 0; j < size; j++) {

                    values.put(CitiesDB.COUNTRY, cities.getString(i));
                    values.put(CitiesDB.CITY, data.getString(j));
                    sqLiteDatabase.insert(TABLE_NAME, null, values);
                }
            }
        } catch (Exception e) {
        }
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
        MainActivity.listHandler.publish(null);
    }
}
