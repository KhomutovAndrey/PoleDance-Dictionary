package com.khomutov_andrey.hom_ai.poledance_dictionary;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hom-ai on 12.10.2015.
 */
public class ImageRoutine {
    //private File file;
    private Context context;
    private Uri mUri=null;
    private String path;
    private String CACH_DIR;

    public ImageRoutine(Context context, String cachDir){
        this.context=context;
        this.CACH_DIR=cachDir;
    }

    /*
    public File getExternalCacheDir(){
        return context.getExternalCacheDir();
    }
    */

    // заменяет метод public File getExternalCacheDir()
    // возвращает рабочий каталог
    public File getCacheDir(){
        //File dir = new File(CACH_DIR);
        return new File(CACH_DIR);
    }

    public void saveImageInit(String directory, String filename, Bitmap bitmap){
        //int i = bitmap.getByteCount();
        File file = new File(directory,filename+".jpg");
        try {
            OutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveImage(String imagename, Bitmap bitmap){
        File file = new File(imagename);
        try {
            OutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public Bitmap getBitmapFromFile(String filename){
        Bitmap bitmap;
        File file = new File(filename);
        try {
            InputStream fis=new FileInputStream(file);
            bitmap = BitmapFactory.decodeFile(file.getPath());
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* public Uri generateFileUri(){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(context.getExternalCacheDir().toString()+File.separator+timeStamp+".jpg");
        return Uri.fromFile(file);
    } */

    public Uri generateFileUri(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(CACH_DIR+File.separator+timeStamp+".jpg");
        return Uri.fromFile(file);
    }

}
