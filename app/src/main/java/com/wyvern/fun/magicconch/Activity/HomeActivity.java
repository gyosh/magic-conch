package com.wyvern.fun.magicconch.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wyvern.fun.magicconch.R;


public class HomeActivity extends ActionBarActivity {
    private ListView mCategoryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeField();
        facadeCategoryList();
    }

    private void initializeField() {
        mCategoryListView = (ListView) findViewById(R.id.categoryList);
    }

    private void facadeCategoryList() {
        String[] categories = new String[]{"Fasilkom Canteen", "Office Canteen", "Where to Hangout"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.category_row, R.id.textView, categories);
        mCategoryListView.setAdapter(adapter);
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
