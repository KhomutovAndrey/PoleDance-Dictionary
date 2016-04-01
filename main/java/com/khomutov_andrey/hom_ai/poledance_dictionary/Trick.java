package com.khomutov_andrey.hom_ai.poledance_dictionary;

import android.widget.Toast;

import java.util.ArrayList;
/**
 * Created by hom-ai on 18.09.2015.
 */
public class Trick {
    private int id; // id из БД
    private String title; // название трюка
    private int sl; // сложность
    private String img1; // имя ресурса изображения превьюшки
    private String img2; // имя ресурса изображения трюка
    private int complete; // отметка о выполнении
    private String tag; // синонимы трюка
    private String content; // описание трюка

    public Trick(int id, String title, int sl, String img1, String img2, int complete, String tag, String content) {
        this.complete = complete;
        this.id = id;
        this.img1 = img1;
        this.img2 = img2;
        this.sl = sl;
        this.tag = tag;
        this.title = title;
        this.content = content;
    }

    public Trick(ArrayList<String> trick){
        this.title=trick.get(0);
        this.sl = Integer.parseInt(trick.get(1), 10);
        this.img1 = trick.get(2);
        this.img2 = trick.get(3);
        this.complete = Integer.parseInt(trick.get(4));
        this.tag = trick.get(5);
        this.content = trick.get(6);
    }

    @Override
    public String toString() {
        return title;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getComplete() {
        return complete;
    }

    public int getId() {
        return id;
    }

    public String getImg1() {
        return img1;
    }

    public String getImg2() {
        return img2;
    }

    public int getSl() {
        return sl;
    }

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getConten() {
        return content;
    }

}
