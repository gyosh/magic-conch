package com.wyvern.fun.magicconch.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by gyosh on 7/18/15.
 */
public class DbAdapter {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
    public static final String DEFAULT_DATE = "2015-01-01 00:00:00";

    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CATEGORY = "categories";
    private static final String TABLE_OPTION = "options";

    public static final String CATEGORY_ROW_ID = "_id";
    public static final String CATEGORY_NAME = "name";
    public static final String CATEGORY_LAST_ACCESS = "last_access";

    public static final String OPTION_ROW_ID = "_id";
    public static final String OPTION_NAME = "name";
    public static final String OPTION_ENABLED = "enabled";
    public static final String OPTION_CATEGORY = "category_id";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    private static final String CREATE_TABLE_CATEGORY =
            "create table " + TABLE_CATEGORY + " ("
                    + CATEGORY_ROW_ID + " integer primary key autoincrement, "
                    + CATEGORY_NAME + " text not null, "
                    + CATEGORY_LAST_ACCESS + " text not null);";
    private static final String CREATE_TABLE_OPTION =
            "create table " + TABLE_OPTION + " ("
                    + OPTION_ROW_ID + " integer primary key autoincrement, "
                    + OPTION_NAME + " text not null, "
                    + OPTION_CATEGORY + " integer, "
                    + OPTION_ENABLED + " integer);";

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DbAdapter open() throws android.database.SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long createCategory(String name) {
        ContentValues category = new ContentValues();
        category.put(CATEGORY_NAME, name);
        category.put(CATEGORY_LAST_ACCESS, getNowAsString());
        return mDb.insert(TABLE_CATEGORY, null, category);
    }

    public boolean deleteCategory(long rowId) {
        return mDb.delete(TABLE_CATEGORY, CATEGORY_ROW_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllCategories() {
        return mDb.query(TABLE_CATEGORY, new String[] {CATEGORY_ROW_ID, CATEGORY_NAME, CATEGORY_LAST_ACCESS},
                null, null, null, null, null);
    }

    public boolean updateCategory(long rowId, String name) {
        ContentValues args = new ContentValues();

        args.put(CATEGORY_NAME, name);
        args.put(CATEGORY_LAST_ACCESS, getNowAsString());
        return mDb.update(TABLE_CATEGORY, args, CATEGORY_ROW_ID + "=" + rowId, null) > 0;
    }

    public long createOption(String name, int categoryRowId){
        ContentValues option = new ContentValues();
        option.put(OPTION_NAME, name);
        option.put(OPTION_ENABLED, 1);
        option.put(OPTION_CATEGORY, categoryRowId);
        return mDb.insert(TABLE_OPTION, null, option);
    }

    public boolean deleteOption(int rowId){
        return mDb.delete(TABLE_OPTION, OPTION_ROW_ID + "=" + rowId, null) > 0;
    }

    public boolean updateOption(long rowId, String name, boolean enabled) {
        ContentValues args = new ContentValues();

        args.put(OPTION_NAME, name);
        args.put(OPTION_ENABLED, enabled ? 1 : 0);
        return mDb.update(TABLE_OPTION, args, OPTION_ROW_ID + "=" + rowId, null) > 0;
    }

    public Cursor fetchOptions(int categoryId) {
        return mDb.query(TABLE_OPTION, new String[] {OPTION_ROW_ID, OPTION_NAME, OPTION_ENABLED},
                OPTION_CATEGORY + "=" + categoryId , null, null, null, null);
    }

    private String getNowAsString() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        return dateFormatter.format(cal.getTime());
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_CATEGORY);
            db.execSQL(CREATE_TABLE_OPTION);
            putInitialData(db);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Not used, but you could upgrade the database with ALTER Scripts
        }

        private void putInitialData(SQLiteDatabase db){
            int optionRow = 0;
            int categoryRow = 0;

            insertCategory(db, ++categoryRow, "Yes/No question");
            insertOption(db, ++optionRow, categoryRow, "Yes!", 1);
            insertOption(db, ++optionRow, categoryRow, "No!", 1);
            insertOption(db, ++optionRow, categoryRow, "Absolutely, baby~", 1);
            insertOption(db, ++optionRow, categoryRow, "Nope...", 1);

            insertCategory(db, ++categoryRow, "What to eat");
            insertOption(db, ++optionRow, categoryRow, "Fried rice", 1);
            insertOption(db, ++optionRow, categoryRow, "Chicken curry", 1);
            insertOption(db, ++optionRow, categoryRow, "Vegetable salad", 1);

            insertCategory(db, ++categoryRow, "Long press me");
            insertOption(db, ++optionRow, categoryRow, "You can disable option", 1);
            insertOption(db, ++optionRow, categoryRow, "Try it by pressing me", 0);
            insertOption(db, ++optionRow, categoryRow, "You can rename/delete option", 1);
            insertOption(db, ++optionRow, categoryRow, "Try it by long-pressing me", 1);
        }

        private void insertCategory(SQLiteDatabase db, int rowId, String name){
            ContentValues category = new ContentValues();
            category.put(CATEGORY_ROW_ID, rowId);
            category.put(CATEGORY_NAME, name);
            category.put(CATEGORY_LAST_ACCESS, DEFAULT_DATE);
            db.insert(TABLE_CATEGORY, null, category);
        }

        private void insertOption(SQLiteDatabase db, int rowId, int categoryRowId, String name, int enabled){
            ContentValues option = new ContentValues();
            option.put(OPTION_ROW_ID, rowId);
            option.put(OPTION_CATEGORY, categoryRowId);
            option.put(OPTION_NAME, name);
            option.put(OPTION_ENABLED, enabled);
            db.insert(TABLE_OPTION, null, option);
        }
    }
}
