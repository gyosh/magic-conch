package com.wyvern.fun.magicconch.Activity;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wyvern.fun.magicconch.Adapter.CategoryArrayAdapter;
import com.wyvern.fun.magicconch.Adapter.DbAdapter;
import com.wyvern.fun.magicconch.Adapter.OptionArrayAdapter;
import com.wyvern.fun.magicconch.Model.Category;
import com.wyvern.fun.magicconch.Model.Option;
import com.wyvern.fun.magicconch.R;

import java.util.ArrayList;
import java.util.List;


public class EditActivity extends ActionBarActivity {
    private ListView mOptionListView;
    private DbAdapter mDbAdapter;
    private ArrayList<Option> options;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        retrieveCategoryId();
        initializeFields();
        mDbAdapter.open();
        populateOptionList();
    }

    private void retrieveCategoryId() {
        Bundle extras = getIntent().getExtras();
        categoryId = extras.getInt(DbAdapter.CATEGORY_ROW_ID);
    }

    private void initializeFields() {
        mOptionListView = (ListView) findViewById(R.id.option_list);
        mDbAdapter = new DbAdapter(this);
        options = new ArrayList<>();
    }

    private void populateOptionList() {
        loadOptions();

        OptionArrayAdapter adapter = new OptionArrayAdapter(this, options);
        mOptionListView.setAdapter(adapter);
    }

    private void loadOptions(){
        options.clear();

        Cursor cursor = mDbAdapter.fetchOptions(categoryId);
        if (cursor.moveToFirst()){ // needed, as explosion occurs if returned empty row
            do {
                int id = cursor.getInt(0);
                String answerText = cursor.getString(1);
                boolean enabled = cursor.getInt(2) > 0;
                options.add(new Option(id, answerText, enabled));
            } while (cursor.moveToNext());
        }

        // quick & dirty, "add new" as list item
        options.add(new Option(-1, "Add new", false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
