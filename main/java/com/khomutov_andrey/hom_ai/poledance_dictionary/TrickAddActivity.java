package com.khomutov_andrey.hom_ai.poledance_dictionary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrickAddActivity extends AppCompatActivity {
    MyDataHelper db;
    private static final int IMAGE_CHOOSE=1;
    private static final int IMAGE_PHOTO=2;
    EditText titleText;
    EditText tagText;
    EditText contentText;
    Spinner slSpiner;
    CheckBox complete;
    ImageView imageView;
    ImageView imageView2;
    boolean editable=true;
    Uri mUri=null;
    //boolean isImageEdit=false;//флаг характеризующий изменялась ли картинка
    Trick trick;
    String CACH_DIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trick_add);
        db = new MyDataHelper(this);

        // получаем рабочий каталог
        SharedPreferences sp = getSharedPreferences(MainActivity.APP_PREFERENCES, MODE_PRIVATE);
        CACH_DIR = sp.getString(MainActivity.SETTINGS_CACH_DIR,"err");
        if(CACH_DIR.equals("err")){
            Toast.makeText(this, R.string.error_CACH_DIR, Toast.LENGTH_LONG).show();
        }

        //находим элементы
        titleText = (EditText)findViewById(R.id.titleText);
        tagText = (EditText)findViewById(R.id.tagText);
        contentText = (EditText)findViewById(R.id.contentText);
        slSpiner = (Spinner)findViewById(R.id.slSpiner);
        complete = (CheckBox)findViewById(R.id.complete);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(openImage);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        imageView2.setOnClickListener(openPhoto);

        //вызываем процедуру заполнения полей данными
        prepareDatalayout();// если через интент не передан id элемента, то процедура ни чего н делает
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trick_add, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_edit:
                editable=true;
                prepareEditActivity(editable);
                invalidateOptionsMenu();
                break;
            case R.id.action_save:
                editable=false;
                //вызов процедуры сохранения
                saveTrick.onClick(null);
                //prepareSaveActivity();
                prepareEditActivity(editable);
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void prepareEditActivity(boolean edit){
        //editable=true;
        titleText.setFocusable(edit);
        titleText.setFocusableInTouchMode(edit);
        tagText.setFocusable(edit);
        tagText.setFocusableInTouchMode(edit);
        contentText.setFocusable(edit);
        contentText.setFocusableInTouchMode(edit);
        slSpiner.setEnabled(edit);
        complete.setEnabled(edit);
        imageView.setEnabled(edit);
        imageView2.setEnabled(edit);
        invalidateOptionsMenu();
    }


    private void prepareSaveActivity(){
        editable=false;
        titleText.setFocusable(false);
        titleText.setFocusableInTouchMode(false);
        tagText.setFocusable(false);
        tagText.setFocusableInTouchMode(false);
        contentText.setFocusable(false);
        contentText.setFocusableInTouchMode(false);
        slSpiner.setEnabled(false);
        complete.setEnabled(false);
        imageView.setEnabled(false);
        imageView2.setEnabled(false);

        invalidateOptionsMenu();
    }

    private void prepareDatalayout(){
        Intent intent = getIntent();
        int trickId = intent.getIntExtra("trick", 0);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.alpha);
        if(trickId>0){
            prepareSaveActivity();//закрываем поля для редактирования, просмотр трюка
            trick = db.findTrickById(trickId);
            if(trick!=null){
                titleText.setText(trick.getTitle());
                tagText.setText(trick.getTag());
                contentText.setText(trick.getConten());
                slSpiner.setSelection(trick.getSl()-1);
                if(trick.getComplete()==1){
                    complete.setChecked(true);
                }else complete.setChecked(false);
                if(trick.getImg1()!=null){
                    ImageRoutine imageRoutine = new ImageRoutine(getApplicationContext(), CACH_DIR);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(imageRoutine.getBitmapFromFile(trick.getImg1()));

                    imageView.setImageDrawable(bitmapDrawable);
                    //Log.d("image", String.valueOf(imageView.getDrawable()));
                    imageView.startAnimation(animation);
                }

            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==IMAGE_CHOOSE && resultCode==RESULT_OK){
            mUri = data.getData();
            String path = mUri.getPath();
            //Log.d("image",mUri.getPath());
            imageView.setImageURI(mUri);
        }
        if(requestCode==IMAGE_PHOTO && resultCode==RESULT_OK){
            //File file = new File(mUri.getPath());
            //ImageRoutine imageRoutine = new ImageRoutine(getApplicationContext());
            //String filename=imageRoutine.generateFileUri().getPath();
            //Log.d("image",mUri.getPath());
            //File file = new File(mUri.getPath());

            imageView.setImageURI(mUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    View.OnClickListener saveTrick = new View.OnClickListener() {//добавить проверку изменялась ли картинка, если не менялась, то сохранять изображение в каталог не нужно
        @Override
        public void onClick(View v) {
            EditText title = (EditText)findViewById(R.id.titleText);
            EditText tag = (EditText)findViewById(R.id.tagText);
            Spinner sl = (Spinner)findViewById(R.id.slSpiner);
            CheckBox complete = (CheckBox)findViewById(R.id.complete);
            ImageView img1 = (ImageView)findViewById(R.id.imageView);
            EditText content = (EditText)findViewById(R.id.contentText);

            if(title.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(),"Не указано наименование элемента",Toast.LENGTH_LONG).show();
                title.requestFocus();
                editable=true;
                return;
            }
            //обрабатываем поле "освоен"
            int comp;
            if(complete.isChecked()){
                comp=1;
            }else comp=0;

            //получаем изображение
            BitmapDrawable drawable = (BitmapDrawable)img1.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ImageRoutine imageRoutine = new ImageRoutine(getApplicationContext(), CACH_DIR);
            String filename=imageRoutine.generateFileUri().getPath();
                if(mUri!=null) {
                    //сохранять по Uri
                    //imageRoutine.saveImage(mUri.getPath(), bitmap);//сохраняем изображение в папку
                    imageRoutine.saveImage(filename, bitmap);
                    //Log.d("image", mUri.getPath());
                    //Log.d("image", imageRoutine.generateFileUri().getPath());
                    if (trick == null) {
                        trick = new Trick(0, title.getText().toString(), sl.getSelectedItemPosition() + 1, filename, null, comp, tag.getText().toString(), content.getText().toString());
                        }else{
                        trick.setTitle(title.getText().toString());
                        trick.setSl(sl.getSelectedItemPosition() + 1);
                        trick.setImg1(filename);
                        trick.setComplete(comp);
                        trick.setTag(tag.getText().toString());
                        trick.setContent(content.getText().toString());
                    }
                }else //сохраняем без картинки
                    if(trick == null) {
                        trick = new Trick(0, title.getText().toString(), sl.getSelectedItemPosition() + 1, null, null, comp, tag.getText().toString(), content.getText().toString());
                    }else{
                        trick.setTitle(title.getText().toString());
                        trick.setSl(sl.getSelectedItemPosition() + 1);
                        //trick.setImg1(mUri.getPath());
                        trick.setComplete(comp);
                        trick.setTag(tag.getText().toString());
                        trick.setContent(content.getText().toString());
                    }
            if(trick.getId()>0){
                db.saveTrick(trick);
                //editable=false;
            }else db.addTrick(trick);
        }
    };

    View.OnClickListener openImage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent,"Выбрать изображение"),IMAGE_CHOOSE);
        }
    };

    View.OnClickListener openPhoto = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mUri=new ImageRoutine(getApplicationContext(),CACH_DIR).generateFileUri();
            //Log.d("image",String.valueOf(mUri));
            //Log.d("image",mUri.getPath());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,mUri);
            startActivityForResult(intent,IMAGE_PHOTO);
        }
    };

}
