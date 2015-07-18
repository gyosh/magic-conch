package com.wyvern.fun.magicconch.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wyvern.fun.magicconch.Adapter.CategoryArrayAdapter;
import com.wyvern.fun.magicconch.Adapter.DbAdapter;
import com.wyvern.fun.magicconch.Model.Category;
import com.wyvern.fun.magicconch.R;

import java.util.ArrayList;


public class HomeActivity extends ActionBarActivity {
    private final int DIALOG_ADD_NEW_CATEGORY = 0;

    private ListView mCategoryListView;
    private DbAdapter mDbAdapter;
    private ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeFields();
        mDbAdapter.open();
        addListener();
    }

    @Override
    protected void onResume(){
        super.onResume();
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
        categories.clear();

        Cursor cursor = mDbAdapter.fetchAllCategories();

        if (cursor.moveToFirst()){ // needed, as explosion occurs if returned empty row
            do{
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String lastAccess = cursor.getString(2);
                categories.add(new Category(id, name, lastAccess));
            }while (cursor.moveToNext());

        }

        // quick & dirty, "add new" as list item
        categories.add(new Category(-1, "Add new", ""));
    }

    private void addListener() {
        registerForContextMenu(mCategoryListView);

        mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Category value = (Category) adapter.getItemAtPosition(position);

                if (isAddButton(value)) {
                    showDialog(DIALOG_ADD_NEW_CATEGORY);

                } else {
                    Intent i = new Intent(HomeActivity.this, AskActivity.class);
                    i.putExtra(DbAdapter.CATEGORY_ROW_ID, value.getId());
                    startActivity(i);
                }
            }

            private boolean isAddButton(Category item) {
                return item.getId() < 0;
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog dialogDetails = null;

        switch (id) {
            case DIALOG_ADD_NEW_CATEGORY:
                LayoutInflater inflater = LayoutInflater.from(this);
                final View dialogView = inflater.inflate(R.layout.add_category_dialog, null);
                final EditText enteredText = (EditText) dialogView.findViewById(R.id.add_category_text);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter List Name");

                builder.setView(dialogView);

                builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Long rowId = mDbAdapter.createCategory("" + enteredText.getText());

                        Intent i = new Intent(HomeActivity.this, EditActivity.class);
                        i.putExtra(DbAdapter.CATEGORY_ROW_ID, rowId);

                        enteredText.setText("");
                        startActivity(i);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        enteredText.setText("");
                        dialog.cancel();
                    }
                });

                dialogDetails = builder.create();
                break;
        }
        return dialogDetails;
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();

        if (((AdapterView.AdapterContextMenuInfo)menuInfo).position == categories.size()-1) {
            // preventing long click on "add category"
            return;
        }

        mi.inflate(R.menu.menu_category_long_press, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)
                        item.getMenuInfo();
        Category category = categories.get((int)info.id);

        switch(item.getItemId()) {
            case R.id.menu_category_delete:
                mDbAdapter.deleteCategory(category.getId());
                populateCategoryList();
                return true;
            case R.id.menu_category_edit_option:
                Intent i = new Intent(this, EditActivity.class);
                i.putExtra(DbAdapter.CATEGORY_ROW_ID, category.getId());
                startActivity(i);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
