package com.khomutov_andrey.hom_ai.poledance_dictionary;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.khomutov_andrey.hom_ai.poledance_dictionary;

import java.util.List;

public class TrickActivity extends AppCompatActivity {
    MyDataHelper db;
    Trick trick;
    boolean editable=false;
    static final String[] sl_string = new String[]{"Начинающие","Продолжающие","Профи"};
    ImageRoutine imgRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trick);
        SharedPreferences sp = getSharedPreferences(MainActivity.APP_PREFERENCES, MODE_PRIVATE);
        String cachDir = sp.getString(MainActivity.SETTINGS_CACH_DIR,"err");

        if(cachDir.equals("err")){
            Toast.makeText(this,R.string.error_CACH_DIR,Toast.LENGTH_LONG).show();
        }

        db = new MyDataHelper(this);
        imgRoutine = new ImageRoutine(this, cachDir);
        Intent intent = getIntent();
        int trickId = intent.getIntExtra("trick",0);
        trick = getTrickbyId(trickId);
        if(trick!=null){
            prepareDatalayout();
        }else Toast.makeText(this,"Не найдено",Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trick, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_edit:
                editable=true;
                prepareEditActivity();
                invalidateOptionsMenu();
                break;
            case R.id.action_save:
                editable=false;
                prepareSaveActivity();
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem edit = menu.findItem(R.id.action_edit);
        MenuItem save = menu.findItem(R.id.action_save);

        if(editable){
            save.setVisible(true);
            edit.setVisible(false);
        }else{
            save.setVisible(false);
            edit.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    private void prepareDatalayout(){
        EditText title = (EditText)findViewById(R.id.titleTrick);
        EditText tagTrick = (EditText)findViewById(R.id.tagTrick);
        Spinner slTrick = (Spinner)findViewById(R.id.slTrick);
        //TextView slTrick2 = (TextView)findViewById(R.id.textsl);
        CheckBox complete = (CheckBox)findViewById(R.id.completeTrick);
        EditText contentTrick = (EditText)findViewById(R.id.contentTrick);
        ImageView img1Trick = (ImageView)findViewById(R.id.img1Trick);

        title.setFocusable(false);
        tagTrick.setFocusable(false);
        contentTrick.setFocusable(false);

        title.setText(trick.getTitle());
        tagTrick.setText(trick.getTag());
        slTrick.setEnabled(false);
        //slTrick.setFocusable(false);
        slTrick.setSelection(trick.getSl() - 1);

        if(trick.getComplete()==1){
            complete.setChecked(true);
        }else complete.setChecked(false);
        contentTrick.setText(trick.getConten());
        if(trick.getImg1()!=null) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(imgRoutine.getBitmapFromFile(trick.getImg1()));
            img1Trick.setImageDrawable(bitmapDrawable);
        }
    }


    private void prepareEditActivity(){
        EditText title = (EditText)findViewById(R.id.titleTrick);
        EditText tagTrick = (EditText)findViewById(R.id.tagTrick);
        Spinner slTrick = (Spinner)findViewById(R.id.slTrick);
        CheckBox complete = (CheckBox)findViewById(R.id.completeTrick);
        EditText contentTrick = (EditText)findViewById(R.id.contentTrick);
        ImageView img1Trick = (ImageView)findViewById(R.id.img1Trick);

        title.setFocusable(true);
        title.setFocusableInTouchMode(true);
        tagTrick.setFocusable(true);
        tagTrick.setFocusableInTouchMode(true);
        slTrick.setEnabled(true);
        complete.setEnabled(true);
        contentTrick.setFocusable(true);
        contentTrick.setFocusableInTouchMode(true);
    }


    private void prepareSaveActivity(){
        EditText title = (EditText)findViewById(R.id.titleTrick);
        EditText tagTrick = (EditText)findViewById(R.id.tagTrick);
        Spinner slTrick = (Spinner)findViewById(R.id.slTrick);
        CheckBox complete = (CheckBox)findViewById(R.id.completeTrick);
        EditText contentTrick = (EditText)findViewById(R.id.contentTrick);
        ImageView img1Trick = (ImageView)findViewById(R.id.img1Trick);

        title.setFocusable(false);
        tagTrick.setFocusable(false);
        slTrick.setEnabled(false);
        complete.setEnabled(false);
        contentTrick.setFocusable(false);

        trick.setTitle(title.getText().toString());
        trick.setTag(tagTrick.getText().toString());
        trick.setSl(slTrick.getSelectedItemPosition() + 1);
        if(complete.isChecked()){
            trick.setComplete(1);
        }else trick.setComplete(0);
        trick.setContent(contentTrick.getText().toString());
        db.saveTrick(trick);

    }


    private Trick getTrickbyId(int id){
        if(id>0){
            Trick trick = db.findTrickById(id);
            return trick;
        }else
            return null;
    }
}
