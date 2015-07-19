package com.wyvern.fun.magicconch.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.wyvern.fun.magicconch.Adapter.CategoryArrayAdapter;
import com.wyvern.fun.magicconch.Adapter.DbAdapter;
import com.wyvern.fun.magicconch.Model.Category;
import com.wyvern.fun.magicconch.R;

import java.util.ArrayList;


public class HomeActivity extends ActionBarActivity {
    private final int DIALOG_ADD_NEW_CATEGORY = 0;
    private final int DIALOG_RENAME_CATEGORY = 1;

    private final String INDEX = "index";

    private ListView mCategoryListView;
    private DbAdapter mDbAdapter;
    private ArrayList<Category> categories;
    private CategoryArrayAdapter categoryArrayAdapter;

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

        categoryArrayAdapter = new CategoryArrayAdapter(this, categories);
        mCategoryListView.setAdapter(categoryArrayAdapter);
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
                    showDialog(DIALOG_ADD_NEW_CATEGORY, Bundle.EMPTY);

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
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        AlertDialog dialogDetails = null;

        switch (id) {
            case DIALOG_ADD_NEW_CATEGORY:
                dialogDetails = getAddNewCategoryDialog();
                showKeyboard();
                break;
            case DIALOG_RENAME_CATEGORY:
                dialogDetails = getRenameCategoryDialog(bundle);
                showKeyboard();
                break;
        }

        return dialogDetails;
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private AlertDialog getAddNewCategoryDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.category_add_dialog, null);
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
        return builder.create();
    }

    private AlertDialog getRenameCategoryDialog(Bundle bundle){
        LayoutInflater inflater = LayoutInflater.from(this);

        final View dialogView = inflater.inflate(R.layout.category_rename_dialog, null);
        final EditText enteredText = (EditText) dialogView.findViewById(R.id.category_rename_text);

        int categoryIndex = bundle.getInt(INDEX);
        final Category category = categoryArrayAdapter.getItem(categoryIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename List");
        builder.setView(dialogView);

        enteredText.setText(category.getName());
        enteredText.setSelection(enteredText.getText().length());

        builder.setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String newName = enteredText.getText().toString();

                category.setName(newName);
                mDbAdapter.updateCategory(category.getId(), category.getName());

                categoryArrayAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                enteredText.setText("");
                dialog.cancel();
            }
        });
        return builder.create();
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
            case R.id.menu_category_rename:
                Bundle extras = new Bundle();
                extras.putInt(INDEX, (int)info.id);
                showDialog(DIALOG_RENAME_CATEGORY, extras);
                return true;
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
