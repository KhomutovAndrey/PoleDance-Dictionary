package com.khomutov_andrey.hom_ai.poledance_dictionary;

/**
 * Created by hom-ai on 17.09.2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyDataHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="dictionary.db";
    private static final String TABLE_NAME="table1";
    private static final String TABLE_TRICKS="tricks";
    private static final String TABLE_TRICKS_RES="tricks_res";
    private static final String TABLE_PLAIN="plain";
    private static final String TABLE_PLAIN_TRICKS="plain_tricks";
    private static final int DATABASE_VERSION=2;
    private Context context;
    private SQLiteDatabase db;
    public String[] sTrics;
    private boolean sl1, sl2, sl3, complete, uncomplete;//флаги фильтрации списка элементов


    public MyDataHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
        this.db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {// переписать под новую базу
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY, title TEXT, sl INTEGER, img1 TEXT, img2 TEXT, complete INTEGER, tag TEXT, content TEXT)");
        //заполнить таблицу данными
        List<Trick> list = new ArrayList<Trick>();
        LoaderTricksFromXml loader = new LoaderTricksFromXml(context);
        list=loader.getList();
        Trick trick;
        ContentValues values = new ContentValues();
        int lenght = list.size();
        for(int i=0; i<lenght; i++){
            trick = list.get(i);
            values.put("title",trick.getTitle());
            values.put("sl",trick.getSl());
            values.put("img1",trick.getImg1());
            values.put("img2",trick.getImg2());
            values.put("complete",trick.getComplete());
            values.put("tag",trick.getTag());
            values.put("content",trick.getConten());
            db.insert(TABLE_NAME,null,values);
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        TABLE_TRICKS: - описание трюка
        id INTEGER PRIMARY KEY первичный ключ,
        title TEXT заголовок/название трюка,
        sl INTEGER сложность,
        complete INTEGER трюк освоен, 0:не освоен; 1:освоен,
        tag TEXT тэги/синонимы трюка,
        content TEXT описание исполнения трюка,
        favorit INTEGER признак избранного, 0:не является; 1;инзбранное,
        training INTEGER признак треннировки, 0: не на тренировке; 1:на треннировке

        TABLE_TRICKS_RES: - ресурсы трюка (картинки, видео)
        id INTEGER PRIMARY KEY первичный ключ,
        type INTEGER тип ресурса, 0:изображение; 1:видео,
        preview INTEGER признак превьюшки, 0:не берётся в качестве превьюшки; 1:может браться как превьюшка,
        uri TEXT uri ресурса,
        id_trick INTEGER внешний ключ,связывает с таблицей TABLE_TRICKS

        TABLE_PLAIN: - план тренровок
         id INTEGER PRIMARY KEY первичный ключ,
         title TEXT название тренировки,
         date INTEGER планируемая дата тренировки,
         complete INTEGER признак выполнения тренировки, 0:не выполнена, 1:выпонена

         TABLE_PLAIN_TRICKS: - содержимое тренировки
         id INTEGER PRIMARY KEY первичный ключ,
         id_trick INTEGER внешний ключ, связывающий с таблицей TABLE_TRICKS,
         id_plain INTEGER внешний ключ, связывающий с таблицей TABLE_PLAIN

         */

        //создаём новые таблицы
        //таблица трюков
        db.execSQL("CREATE TABLE " + TABLE_TRICKS + "(id INTEGER PRIMARY KEY, title TEXT, sl INTEGER, complete INTEGER, tag TEXT, content TEXT, favorit INTEGER, training INTEGER)");
        //таблица ресурсов для трюков (URI изображений и видео)
        db.execSQL("CREATE TABLE " + TABLE_TRICKS_RES + "(id INTEGER PRIMARY KEY, type INTEGER, preview INTEGER, uri TEXT, id_trick INTEGER)");
        //таблица планов тренировок
        db.execSQL("CREATE TABLE " + TABLE_PLAIN + "(id INTEGER PRIMARY KEY, title TEXT, date INTEGER, complete INTEGER)");
        //таблица содержимого пана тренировок (список трюков для кадого плана)
        db.execSQL("CREATE TABLE " + TABLE_PLAIN_TRICKS + "(id INTEGER PRIMARY KEY, id_trick INTEGER, id_plain INTEGER)");

        List<ContentValues> list_old = new ArrayList<ContentValues>(); // список значений ContentValues из старой таблицы
        List<ContentValues> list_tricks = new ArrayList<ContentValues>(); // список значений ContentValues для талицы TABLE_TRICKS
        ContentValues trick_Values = new ContentValues(); // значения для таблицы TABLE_TRICKS
        ContentValues trickRes_Values = new ContentValues(); // значения для таблицы TABLE_TRICKS_RES
        ContentValues oldValues = new ContentValues(); // значения из старой таблицы
        long id_trick; // id вставленной записи в таблицу TABLE_TRICKS для формирования внешнего ключа таблицы TABLE_TRICKS_RES

        //читаем все записи из таблицы прежней версии
        Cursor cursor_old = db.query(TABLE_NAME, null, null,null,null,null,null);
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if(cursor_old.moveToFirst()){
            list_old.clear();
            do{
                /*
                //считываем значения из старой таблицы
                oldValues.put("id",cursor_old.getInt(0));// id первичный ключ
                oldValues.put("title",cursor_old.getString(1));//
                oldValues.put("sl",cursor_old.getInt(2));//
                oldValues.put("img1",cursor_old.getString(3));//
                oldValues.put("img2",cursor_old.getString(4));//
                oldValues.put("complete", cursor_old.getInt(5));//
                oldValues.put("tag", cursor_old.getString(6));//
                oldValues.put("content", cursor_old.getString(7));//
                */
                //list_old.add(oldValues);
                //формируем значения для таблицы TABLE_TRICKS
                trick_Values.put("title",cursor_old.getString(1));// title трюка
                trick_Values.put("sl",cursor_old.getString(2));// title трюка
                trick_Values.put("complete",cursor_old.getString(5));// title трюка
                trick_Values.put("tag",cursor_old.getString(6));// title трюка
                trick_Values.put("content",cursor_old.getString(7));//
                trick_Values.put("favorites",0);//
                trick_Values.put("training",0);//
                //trick_Values.put("t",cursor_old.getInt(0));
                //добавляем данные в TABLE_TRICKS
                id_trick = db.insert(TABLE_TRICKS, null, trick_Values); // запоминаем id вставленной записи
                //формируем значения для таблицы TABLE_TRICKS_RES
                trickRes_Values.put("type",0); // тип ресурса 0:изображение; 1:видео
                trickRes_Values.put("uri", cursor_old.getString(3));
                trickRes_Values.put("id_trick", id_trick);
                trickRes_Values.put("preview", 1); // признак preview, если 1: ресурс будет браться как превью; 0: обычный ресурс
                db.insert(TABLE_TRICKS_RES,null,trickRes_Values);
                //list_tricks.add(trick_Values);

            }while (cursor_old.moveToNext());
        }

    }


    public List<Trick> selectAll(String tag, boolean sl1, boolean sl2, boolean sl3, boolean complete, boolean uncomplete, int favorites, int training){//если tag не пуст, то выводим все записи (соответственно нстройкам отображения, с содержащимся тэгом)
        /*
        выдаёт все элементы
         */

        /*
        //считываем настройки (условия отображения элементов) из файла настроек
        SharedPreferences mSettings = context.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
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
        */
        String s,where_s1,where;
        int int_sl1=(sl1)?1:-1;
        int int_sl2=(sl2)?2:-1;
        int int_sl3=(sl3)?3:-1;
        int int_complete1=(complete)?1:-1;//если в настройках указано отображать завершённые, то выводим элементы с полем complete=1, иначе complete=-1 (таких в базе нет)
        int int_uncomplete1=(uncomplete)?0:-1;//если в настройках указано отображать незавершённые, то выводим элементы с полем complete=0, иначе complete=-1 (таких в базе нет)


        where="sl in ("+int_sl1+","+int_sl2+","+int_sl3+") and complete in ("+int_complete1+","+int_uncomplete1+")" ;
        if(tag!=null){
            where=where+" and "+"( "+"tag like '"+"%"+tag+"%"+"'" +" or title like '"+"%"+tag+"%"+"')";
        }
        String[] selectionsArgs={};
        String[] orderBy={};

        List<Trick> list = new ArrayList<Trick>();
        Cursor cursor = db.query(TABLE_NAME, null, where,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Trick trick = new Trick(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4),cursor.getInt(5),cursor.getString(6),cursor.getString(7));
                list.add(trick); //создать объект и заполнить его поля
            }while(cursor.moveToNext());
        }
        return list;
    }


    public Trick findTrickById(int id){
        Trick trick;
        Cursor cursor = db.query(TABLE_NAME, null,"id="+id,null,null,null,null);
        if(cursor.moveToFirst()){
            trick = new Trick(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4),cursor.getInt(5),cursor.getString(6), cursor.getString(7));
        }else trick=null;
        return trick;
    }


    public boolean saveTrick(Trick trick){
        ContentValues values = new ContentValues();
        values.put("title",trick.getTitle());
        values.put("sl", trick.getSl());
        values.put("img1",trick.getImg1());// должен быть полное имя файла изображения
        values.put("img2",trick.getImg2());
        values.put("complete",trick.getComplete());
        values.put("tag",trick.getTag());
        values.put("content",trick.getConten());
        if (db.update(TABLE_NAME, values, "id=" + trick.getId(), null)>0){
            return true;
        }else return false;
    }

    //перезаписать в поле img1 вместо названий картинок полные имена файлов
    public void firstRun(String cachDir){
        //String path = context.getExternalCacheDir().toString()+ File.separator;//каталог с картинками
        String path = cachDir + File.separator;//каталог с картинками

        List<Trick> list = new ArrayList<Trick>();
        list = selectAll(null, true, true, true, true, true);
        Trick trick;
        ContentValues values = new ContentValues();
        int lenght = list.size();
        for(int i=0; i<lenght; i++){
            trick = list.get(i);
            values.put("img1",path+trick.getImg1()+".jpg");//формируем полное имя файла для сохранения в БД вместо имени из ресурсов
            int l=db.update(TABLE_NAME, values, "id=" + trick.getId(), null);
        }
    }


    public void deleteTrick(Trick trick){
        if(trick!=null){
            db.delete(TABLE_NAME,"id="+trick.getId(),null);
        }
    }


    public boolean addTrick(Trick trick){
            ContentValues values = new ContentValues();
            values.put("title", trick.getTitle());
            values.put("sl", trick.getSl());
            values.put("img1", trick.getImg1());// должен быть полное имя файла изображения
            values.put("img2", trick.getImg2());
            values.put("complete", trick.getComplete());
            values.put("tag", trick.getTag());
            values.put("content", trick.getConten());

            long i= db.insert(TABLE_NAME,null,values);
        if(i!=-1){
            return true;
        }else return false;
    }

    public  List<ContentValues> test(){
        List<ContentValues> list = new ArrayList<ContentValues>();
        ContentValues newValues = new ContentValues();
        //читаем все записи из таблицы прежней версии
        Cursor cursor_old = db.query(TABLE_NAME, null, null,null,null,null,null);
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if(cursor_old.moveToFirst()){
            list.clear();
            do{//считываем значения из старой таблицы
                newValues.put("id",cursor_old.getInt(0));// id первичный ключ
                newValues.put("title",cursor_old.getString(1));//
                newValues.put("sl",cursor_old.getInt(2));//
                newValues.put("img1",cursor_old.getString(3));//
                newValues.put("img2",cursor_old.getString(4));//
                newValues.put("complete",cursor_old.getInt(5));//
                newValues.put("tag",cursor_old.getString(6));//
                newValues.put("content",cursor_old.getString(7));//
                list.add(newValues);
            }while (cursor_old.moveToNext());
        }
        return list;
    }

}
