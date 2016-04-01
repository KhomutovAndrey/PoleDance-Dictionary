package com.khomutov_andrey.hom_ai.poledance_dictionary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences mSettings;
    CheckBox checkBox_sl1;
    CheckBox checkBox_sl2;
    CheckBox checkBox_sl3;
    CheckBox checkBox_complete;
    CheckBox checkBox_uncomplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSettings=getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        checkBox_sl1 = (CheckBox)findViewById(R.id.checkBox_sl1);
        checkBox_sl2 = (CheckBox)findViewById(R.id.checkBox_sl2);
        checkBox_sl3 = (CheckBox)findViewById(R.id.checkBox_sl3);
        checkBox_complete = (CheckBox)findViewById(R.id.checkBox_comlete);
        checkBox_uncomplete = (CheckBox)findViewById(R.id.checkBox_uncomlete);

        checkBox_sl1.setOnClickListener(this);
        checkBox_sl2.setOnClickListener(this);
        checkBox_sl3.setOnClickListener(this);
        checkBox_complete.setOnClickListener(this);
        checkBox_uncomplete.setOnClickListener(this);

        prepareFromPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareFromPreferences();
        TextView ver = (TextView)findViewById(R.id.textView_ver);
        ver.setText("ver. "+ getString(R.string.versionName));
    }

    private void prepareFromPreferences(){
        boolean flag;
        if(mSettings.contains(MainActivity.SETTINGS_SL_1)){
            flag=mSettings.getBoolean(MainActivity.SETTINGS_SL_1,true);
            checkBox_sl1.setChecked(flag);}
        if(mSettings.contains(MainActivity.SETTINGS_SL_2)){
            flag=mSettings.getBoolean(MainActivity.SETTINGS_SL_2,true);
            checkBox_sl2.setChecked(flag);}
        if(mSettings.contains(MainActivity.SETTINGS_SL_3)){
            flag=mSettings.getBoolean(MainActivity.SETTINGS_SL_3,true);
            checkBox_sl3.setChecked(flag);}

        if(mSettings.contains(MainActivity.SETTINGS_COMPLETE)){
            flag=mSettings.getBoolean(MainActivity.SETTINGS_COMPLETE,true);
            checkBox_complete.setChecked(flag);}
        if(mSettings.contains(MainActivity.SETTINGS_UNCOMPLETE)){
            flag=mSettings.getBoolean(MainActivity.SETTINGS_UNCOMPLETE,true);
            checkBox_uncomplete.setChecked(flag);}
    }


     @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(MainActivity.SETTINGS_SL_1, checkBox_sl1.isChecked());
        editor.putBoolean(MainActivity.SETTINGS_SL_2, checkBox_sl2.isChecked());
        editor.putBoolean(MainActivity.SETTINGS_SL_3, checkBox_sl3.isChecked());
        editor.putBoolean(MainActivity.SETTINGS_COMPLETE, checkBox_complete.isChecked());
        editor.putBoolean(MainActivity.SETTINGS_UNCOMPLETE, checkBox_uncomplete.isChecked());
        editor.apply();
    }
}
