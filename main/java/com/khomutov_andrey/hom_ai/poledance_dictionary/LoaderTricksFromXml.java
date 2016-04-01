package com.khomutov_andrey.hom_ai.poledance_dictionary;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hom-ai on 30.09.2015.
 */
public class LoaderTricksFromXml {
    private static ArrayList<Trick> list;
    private static Context context;

    public LoaderTricksFromXml(Context context) {

        XmlPullParser parser = context.getResources().getXml(R.xml.tricks);
        list = new ArrayList<Trick>();
        try {
            while (parser.getEventType() !=XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType()== XmlPullParser.START_TAG && parser.getName().equals("trick")){
                    ArrayList<String> buf = new ArrayList<String>();
                    buf.add(parser.getAttributeValue(0));
                    buf.add(parser.getAttributeValue(1));
                    buf.add(parser.getAttributeValue(2));
                    buf.add(parser.getAttributeValue(3));//img1
                    buf.add(parser.getAttributeValue(4));
                    buf.add(parser.getAttributeValue(5));
                    buf.add(parser.getAttributeValue(6));
                    Trick trick = new Trick(buf);
                    //Log.d("trick",trick.getTitle());
                    if(trick!=null){
                        list.add(trick);
                    }
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Trick> getList(){
        return list;
    }
}
