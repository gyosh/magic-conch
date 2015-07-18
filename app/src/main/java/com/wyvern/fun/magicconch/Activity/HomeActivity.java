package com.wyvern.fun.magicconch.Activity;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wyvern.fun.magicconch.Adapter.CategoryArrayAdapter;
import com.wyvern.fun.magicconch.Adapter.DbAdapter;
import com.wyvern.fun.magicconch.Model.Category;
import com.wyvern.fun.magicconch.R;

import java.util.ArrayList;


public class HomeActivity extends ActionBarActivity {
    private ListView mCategoryListView;
    private DbAdapter mDbAdapter;
    private ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeFields();
        mDbAdapter.open();
        populateCategoryList();
    }

    private void populateCategoryList() {
        loadCategories();
        CategoryArrayAdapter adapter = new CategoryArrayAdapter(this, categories);
        mCategoryListView.setAdapter(adapter);
    }

    private void initializeFields() {
        mCategoryListView = (ListView) findViewById(R.id.category_list);
        mDbAdapter = new DbAdapter(this);
        categories = new ArrayList<>();
    }

    private void loadCategories() {
        Cursor cursor = mDbAdapter.fetchAllCategories();
        cursor.moveToFirst();

        do{
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String lastAccess = cursor.getString(2);
            categories.add(new Category(id, name, lastAccess));
        }while (cursor.moveToNext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
