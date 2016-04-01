package com.khomutov_andrey.hom_ai.poledance_dictionary;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private MyDataHelper db;
    private List<Trick> trickList;
    public String EXTRA_MESSAGE = "trick";
    private MyArrayAdapter adapter;
    private ListView list;
    ImageRoutine imgRoutine;

    //Константы файла настроек
    public static final String APP_PREFERENCES = "setting";//имя настроек
    public static final String PREV_INIT = "init";//флаг первой инициализации
    public static final String CURRENT_ITEM = "curent_item";//позиция текущего выделенного элемента списка
    public static final String SETTINGS_CACH_DIR = "cach_dir";//флаг первой инициализации
    public static final String SETTINGS_SL_1 = "sl_1";// настройка уровень сложности новички; хранит 0 или 1
    public static final String SETTINGS_SL_2 = "sl_2";// настройка уровень сложности продолжающие; хранит 0 или 1
    public static final String SETTINGS_SL_3 = "sl_3";// настройка уровень сложности профи; хранит 0 или 1
    public static final String SETTINGS_COMPLETE = "complete";// настройка трюк выполнен; хранит 0 или 1
    public static final String SETTINGS_UNCOMPLETE = "uncomplete";// настройка трюк не выполнен; хранит 0 или 1

    //public static final String SETTINGS_TAG="tag";//настройка тэг
    private SharedPreferences mSettings; //файл настроек
    private EditText tag_text; //поле поиска и фильтрации
    private String tag;
    private String CACH_DIR; //хранилище приложения (Internal Storage, External Storage)
    boolean sl1, sl2, sl3, complete, uncomplete;//флаги фильтрации списка элементов



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем элементы GUI
        tag_text = (EditText) findViewById(R.id.editText_Tag);
        tag = tag_text.getText().toString();
        ImageButton btn_tag = (ImageButton) findViewById(R.id.imageButton_Tag);
        btn_tag.setOnClickListener(findTag);
        list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(myOnItemClickListenr);
        registerForContextMenu(list);
        //final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //считываем настройки (условия отображения элементов) из файла настроек
        if(mSettings.contains(MainActivity.SETTINGS_SL_1)){
            sl1=mSettings.getBoolean(MainActivity.SETTINGS_SL_1,true);
        }else sl1=true;
        if(mSettings.contains(MainActivity.SETTINGS_SL_2)){
            sl2=mSettings.getBoolean(MainActivity.SETTINGS_SL_2,true);
        }else sl2=true;
        if(mSettings.contains(MainActivity.SETTINGS_SL_3)){
            sl3=mSettings.getBoolean(MainActivity.SETTINGS_SL_3,true);
        }else sl3=true;
        if(mSettings.contains(MainActivity.SETTINGS_COMPLETE)){
            complete=mSettings.getBoolean(MainActivity.SETTINGS_COMPLETE,true);
        }else complete=true;
        if(mSettings.contains(MainActivity.SETTINGS_COMPLETE)){
            uncomplete=mSettings.getBoolean(MainActivity.SETTINGS_UNCOMPLETE,true);
        }else uncomplete=true;

        db = new MyDataHelper(this);

        // инициализируем Вкладки
        initTab();

        //определяем рабочий каталог приложения external|internal
        //получаем каталог приложения Выбор между внутренним и внешним хранилищем (Internal Storage, External Storage)
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            CACH_DIR = getExternalCacheDir().toString(); // каталог на внешней карте
        } else CACH_DIR = getCacheDir().toString(); //каталог на внутренней карте
        //первый запуск приложения
        // сохраняем в настройки рабочий каталог
        // сохраняем изображения из ресурсов в файлы рабочего каталога
        if (!mSettings.contains(PREV_INIT)) {
            mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
            mSettings.edit().putString(SETTINGS_CACH_DIR, CACH_DIR).commit();
            //показать заставку о первом запуске с прогрессом
            imgRoutine = new ImageRoutine(this, CACH_DIR);
            initImageFile();
        } else {
            CACH_DIR = mSettings.getString(SETTINGS_CACH_DIR, CACH_DIR);
            imgRoutine = new ImageRoutine(this, CACH_DIR);
        }

        if (tag.isEmpty()) {
            trickList = db.selectAll(null, sl1, sl2, sl3, complete, uncomplete);//получаем список трюков
        } else trickList = db.selectAll(tag, sl1, sl2, sl3, complete, uncomplete);//получаем список трюков

        adapter = new MyArrayAdapter(this, R.layout.my_list, trickList);
        list.setAdapter(adapter);


        //getTrickList();
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setCurrentTabByTag("tab1");
    }


    @Override
    protected void onResume() {
        super.onResume();
        tag = tag_text.getText().toString();
        //считываем настройки (условия отображения элементов) из файла настроек
        if(mSettings.contains(MainActivity.SETTINGS_SL_1)){
            sl1=mSettings.getBoolean(MainActivity.SETTINGS_SL_1,true);
        }else sl1=true;
        if(mSettings.contains(MainActivity.SETTINGS_SL_2)){
            sl2=mSettings.getBoolean(MainActivity.SETTINGS_SL_2,true);
        }else sl2=true;
        if(mSettings.contains(MainActivity.SETTINGS_SL_3)){
            sl3=mSettings.getBoolean(MainActivity.SETTINGS_SL_3,true);
        }else sl3=true;
        if(mSettings.contains(MainActivity.SETTINGS_COMPLETE)){
            complete=mSettings.getBoolean(MainActivity.SETTINGS_COMPLETE,true);
        }else complete=true;
        if(mSettings.contains(MainActivity.SETTINGS_COMPLETE)){
            uncomplete=mSettings.getBoolean(MainActivity.SETTINGS_UNCOMPLETE,true);
        }else uncomplete=true;

        if (tag.isEmpty()) {
            trickList = db.selectAll(null,sl1, sl2, sl3, complete, uncomplete);//получаем список трюков
        } else trickList = db.selectAll(tag,sl1, sl2, sl3, complete, uncomplete);//получаем список трюков

        //adapter = new MyArrayAdapter(this, R.layout.my_list, trickList);
        //list.setAdapter(adapter);
        adapter.setData(trickList);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        //устанавливаем активным элемент списка на котором были до перехода
        if (mSettings.contains(CURRENT_ITEM)) {
            int i = mSettings.getInt(CURRENT_ITEM, 0);
            list.setSelection(i);
        }
    }

    //возвращает список трюков в соответствии с выбранной вкладкой
    //для работы с TabHost
    public List<Trick> getTrickList(boolean complete, boolean uncomplete, boolean favorits){//когда добавится поле избранное - обработать оператор
        tag = tag_text.getText().toString();
        //считываем настройки (условия отображения элементов) из файла настроек
        if(mSettings.contains(MainActivity.SETTINGS_SL_1)){
            sl1=mSettings.getBoolean(MainActivity.SETTINGS_SL_1,true);
        }else sl1=true;
        if(mSettings.contains(MainActivity.SETTINGS_SL_2)){
            sl2=mSettings.getBoolean(MainActivity.SETTINGS_SL_2,true);
        }else sl2=true;
        if(mSettings.contains(MainActivity.SETTINGS_SL_3)){
            sl3=mSettings.getBoolean(MainActivity.SETTINGS_SL_3,true);
        }else sl3=true;

        if (tag.isEmpty()) {
            trickList = db.selectAll(null,sl1, sl2, sl3, complete, uncomplete);//получаем список трюков
        } else trickList = db.selectAll(tag,sl1, sl2, sl3, complete, uncomplete);//получаем список трюков

        return trickList;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);
        //switch (id){
        //    case INIT_DIALOG:
        //mProgressDialog = new ProgressDialog(this);
        //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //mProgressDialog.setTitle("Первый запуск приложения");
        //return mProgressDialog;
        //    default: //return null;
        //}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextmenu_tricklist, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Trick trick = trickList.get(cmi.position);
        //Trick trick = trickList.get(position);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(CURRENT_ITEM, cmi.position);//запоминаем какой номер элемента списка был выбран
        editor.apply();
        switch (item.getItemId()) {
            case R.id.action_add:
                addTrick();
                break;
            case R.id.action_delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Удаление");
                dialog.setMessage("Удалить элемент" + trick.getTitle() + "?");
                dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Trick tr = trick;
                        deleteTrick(tr);
                    }
                });
                dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                break;
            case R.id.action_edt:
                openTrick(trick);
                break;
        }
        return super.onContextItemSelected(item);
    }

    //выбор пункта контекстного меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addTrick:
                addTrick();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_quit:
                //finish();
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initImageFile() {
        SharedPreferences.Editor editor = mSettings.edit();
        //сохранить в файл настроек все опции отмеченными
        //
        Bitmap bitmap;
        String imagename;
        List<Trick> list = new ArrayList<Trick>();
        LoaderTricksFromXml loader = new LoaderTricksFromXml(this);
        list = loader.getList();
        for (int i = 0; i < list.size(); i++) {
            imagename = list.get(i).getImg1();
            if (imagename != null) {
                int id = this.getResources().getIdentifier(imagename, "drawable", "com.khomutov_andrey.hom_ai.poledance_dictionary");
                bitmap = BitmapFactory.decodeResource(getResources(), id);
                //String directory = this.getExternalCacheDir().toString(); //получаем директорию на внешнем диске
                imgRoutine.saveImageInit(CACH_DIR, imagename, bitmap);
                //imgRoutine.saveImage(imagename, bitmap);
            }
        }
        db.firstRun(CACH_DIR);
        editor.putBoolean(PREV_INIT, true);
        editor.apply();
    }


    //обработка нажатия элемента списка
    OnItemClickListener myOnItemClickListenr = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Trick trick = trickList.get(position);
            openTrick(trick);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt(CURRENT_ITEM, position);//запоминаем какой номер элемента списка был выбран
            editor.apply();
        }
    };


    View.OnClickListener findTag = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tag = tag_text.getText().toString();
            if (tag.isEmpty()) {
                trickList = db.selectAll(null, sl1, sl2, sl3, complete, uncomplete);//получаем список трюков
            } else trickList = db.selectAll(tag, sl1, sl2, sl3, complete, uncomplete);//получаем список трюков

            //adapter = new MyArrayAdapter(getApplicationContext(),R.layout.my_list,trickList); //11.12.2015 попробовать обновить адаптер
            //list.setAdapter(adapter); //11.12.2015 попробовать обновить адаптер
            //adapter.clear();
            adapter.setData(trickList);
            //adapter.addAll(trickList);
            adapter.notifyDataSetChanged();

            //list.setSelection(1);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.remove(CURRENT_ITEM);
            editor.apply();

        }
    };

    // Открыть экран добавления нового элемента
    private void addTrick() {
        Intent intent = new Intent(getApplicationContext(), TrickAddActivity.class);
        startActivity(intent);
    }

    //открыть экран просмотра элемента
    private void openTrick(Trick trick) {
        Intent intent = new Intent(getApplicationContext(), TrickAddActivity.class);
        intent.putExtra(EXTRA_MESSAGE, trick.getId());
        startActivity(intent);
    }


    //Удаление элемента
    private void deleteTrick(Trick trick) {
        db.deleteTrick(trick);
        onResume();
    }


    public List<Trick> getTrickList() {
        return trickList;
    }


    private void initTab() {
        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tab1");
        tabSpec.setIndicator(getString(R.string.tabSpec1));
        tabSpec.setContent(R.id.listView);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setIndicator(getString(R.string.tabSpec2));
        tabSpec.setContent(R.id.listView);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab3");
        tabSpec.setIndicator(getString(R.string.tabSpec3));
        tabSpec.setContent(R.id.listView);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("tab3");

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                switch (s){
                    case "tab1"://Toast.makeText(getBaseContext(),"tab=1", Toast.LENGTH_SHORT).show();
                        adapter.setData(getTrickList(true, true, true));
                        adapter.notifyDataSetChanged();
                        list.setAdapter(adapter);
                        db.test();
                        break;
                    case "tab2"://Toast.makeText(getBaseContext(),"tab=2", Toast.LENGTH_SHORT).show();
                        adapter.setData(getTrickList(true, false, true));
                        adapter.notifyDataSetChanged();
                        list.setAdapter(adapter);
                        break;
                    case "tab3"://Toast.makeText(getBaseContext(),"tab="+s, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        });
    }


    private class MyArrayAdapter extends ArrayAdapter<Trick> {
        private Context context;
        private List<Trick> val;

        public MyArrayAdapter(Context context, int resource, List<Trick> objects) {
            super(context, R.layout.my_list, objects);
            this.context = context;
            this.val = objects;
        }

        public Context getCnt() {
            return context;
        }

        public void setData(List<Trick> data) {
            //this.val=data;
            clear();
            if (data != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    addAll(data);
                } else {
                    for (Trick tr : data) {
                        add(tr);
                    }
                }
            }
        }

        @Override
        //получаем кастомную строку для ListView трюков
        public View getView(int position, View convertView, ViewGroup parent) {
            Trick trick = val.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.my_list, parent, false);
            TextView textRow = (TextView) rowView.findViewById(R.id.textRow);
            ImageView imgRow = (ImageView) rowView.findViewById(R.id.imgRow);
            CheckBox checkRow = (CheckBox) rowView.findViewById(R.id.checkBoxRow);
            TextView textRatingRow = (TextView) rowView.findViewById(R.id.textRatingRow);
            textRow.setText(val.get(position).getTitle());

            if (trick.getImg1() != null) {
                //BitmapDrawable bitmapDrawable = new BitmapDrawable(imgRoutine.getBitmapFromFile(context.getExternalCacheDir().toString(),trick.getImg1()+".jpg"));
                BitmapDrawable bitmapDrawable = new BitmapDrawable(imgRoutine.getBitmapFromFile(trick.getImg1()));
                imgRow.setImageDrawable(bitmapDrawable);
            }

            switch (val.get(position).getSl()) {
                case 1:
                    textRatingRow.setText("Уровень: начинающие");
                    break;
                case 2:
                    textRatingRow.setText("Уровень: продолжающие");
                    break;
                case 3:
                    textRatingRow.setText("Уровень: профи");
                    break;
            }

            if (trick.getComplete() == 1) {
                checkRow.setChecked(true);
            } else checkRow.setChecked(false);

            return rowView;

        }
    }


}
