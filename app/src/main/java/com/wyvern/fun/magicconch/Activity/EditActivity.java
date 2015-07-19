package com.wyvern.fun.magicconch.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.wyvern.fun.magicconch.Adapter.DbAdapter;
import com.wyvern.fun.magicconch.Adapter.OptionArrayAdapter;
import com.wyvern.fun.magicconch.Model.Option;
import com.wyvern.fun.magicconch.R;

import java.util.ArrayList;


public class EditActivity extends ActionBarActivity {
    private static final int DIALOG_ADD_NEW_OPTION = 0;
    private static final int DIALOG_RENAME_OPTION = 1;
    private static final String INDEX = "index";

    private ListView mOptionListView;
    private DbAdapter mDbAdapter;
    private ArrayList<Option> options;
    private OptionArrayAdapter optionArrayAdapter;
    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        retrieveCategoryId();
        initializeFields();
        mDbAdapter.open();
        populateOptionList();
        addListener();
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

        optionArrayAdapter = new OptionArrayAdapter(this, options);
        mOptionListView.setAdapter(optionArrayAdapter);
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

    private void addListener(){
        registerForContextMenu(mOptionListView);

        mOptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Option value = (Option) adapter.getItemAtPosition(position);

                if (isAddButton(value)) {
                    showDialog(DIALOG_ADD_NEW_OPTION, Bundle.EMPTY);
                } else {
                    value.setEnabled(!value.isEnabled());
                    mDbAdapter.updateOption(value.getId(), value.getName(), value.isEnabled());
                    optionArrayAdapter.notifyDataSetChanged();
                }
            }

            private boolean isAddButton(Option item) {
                return item.getId() < 0;
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
        AlertDialog dialogDetails = null;

        switch (id){
            case DIALOG_ADD_NEW_OPTION:
                dialogDetails = getAddNewOptionDialog();
                showKeyboard();
                break;
            case DIALOG_RENAME_OPTION:
                dialogDetails = getRenameOptionDialog(bundle);
                showKeyboard();
                break;
        }
        
        return dialogDetails;
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private AlertDialog getAddNewOptionDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.option_add_dialog, null);
        final EditText enteredText = (EditText) dialogView.findViewById(R.id.add_option_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Option");
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Long rowId = mDbAdapter.createOption("" + enteredText.getText(), categoryId);
                enteredText.setText("");
                populateOptionList();
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

    private AlertDialog getRenameOptionDialog(Bundle bundle){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.option_rename_dialog, null);
        final EditText enteredText = (EditText) dialogView.findViewById(R.id.option_rename_text);

        int optionIndex = bundle.getInt(INDEX);
        final Option option = optionArrayAdapter.getItem(optionIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Option");
        builder.setView(dialogView);

        enteredText.setText(option.getName());
        enteredText.setSelection(enteredText.getText().length());

        builder.setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String newName = enteredText.getText().toString();

                option.setName(newName);
                mDbAdapter.updateOption(option.getId(), option.getName(), option.isEnabled());

                enteredText.setText("");
                optionArrayAdapter.notifyDataSetChanged();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();

        if (((AdapterView.AdapterContextMenuInfo)menuInfo).position == options.size()-1) {
            // preventing long click on "add option"
            return;
        }

        mi.inflate(R.menu.menu_option_long_press, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)
                        item.getMenuInfo();
        Option option = options.get((int)info.id);

        switch(item.getItemId()) {
            case R.id.menu_option_rename:
                Bundle extras = new Bundle();
                extras.putInt(INDEX, (int)info.id);
                showDialog(DIALOG_RENAME_OPTION, extras);
                return true;

            case R.id.menu_option_delete:
                mDbAdapter.deleteOption(option.getId());
                populateOptionList();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
