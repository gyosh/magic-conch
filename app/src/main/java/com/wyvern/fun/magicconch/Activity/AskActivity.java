package com.wyvern.fun.magicconch.Activity;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wyvern.fun.magicconch.Adapter.DbAdapter;
import com.wyvern.fun.magicconch.Model.Category;
import com.wyvern.fun.magicconch.R;

import java.util.ArrayList;
import java.util.Random;


public class AskActivity extends ActionBarActivity {
    private String[] cotys = new String[]{"I think...", "How about", "The answer is", "I'll suggest", "My advice is"};

    private DbAdapter mDbAdapter;
    private Button mAskButton;
    private TextView mCotyTextView;
    private TextView mAnswerTextView;

    private int categoryId;
    private ArrayList<String> answers;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        initializeFields();
        retrieveCategoryId();
        mDbAdapter.open();
        loadPossibleAnswers();
        registerButtonListener();
    }

    private void initializeFields() {
        mDbAdapter = new DbAdapter(this);

        mCotyTextView = (TextView) findViewById(R.id.coty);
        mAnswerTextView = (TextView) findViewById(R.id.answer);
        mAskButton = (Button) findViewById(R.id.ask_button);

        mCotyTextView.setText(" ");
        mAnswerTextView.setText(" ");

        answers = new ArrayList<>();
        random = new Random();
    }

    private void retrieveCategoryId() {
        Bundle extras = getIntent().getExtras();
        categoryId = extras.getInt(DbAdapter.CATEGORY_ROW_ID);
    }

    private void loadPossibleAnswers() {
        Cursor cursor = mDbAdapter.fetchOptions(categoryId);
        cursor.moveToFirst();

        do {
            String answerText = cursor.getString(1);
            boolean enabled = cursor.getInt(2) > 0;
            if (enabled){
                answers.add(answerText);
            }
        } while (cursor.moveToNext());
    }

    private void registerButtonListener(){
        mAskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCotyTextView.setText(getRandomCoty());
                mAnswerTextView.setText(getRandomAnswer());
            }
        });
    }

    private String getRandomCoty() {
        return cotys[random.nextInt(cotys.length)];
    }

    private String getRandomAnswer() {
        String ret = "";
        if (answers.size() == 0){
            ret = "I don't know, please add or enable me some options...";
        }else{
            ret = answers.get(random.nextInt(answers.size()));
        }
        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ask, menu);
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
