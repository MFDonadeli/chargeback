package br.com.mfdonadeli.chargeback;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by mfdonadeli on 3/11/16.
 */
public class JSonNoticeUrlReader {
    private String[] mReturn;
    int mCont = 0;
    public String[] getReturn(String sJsonStr){
        mReturn = new String[7];
        JsonReader reader = new JsonReader(new StringReader(sJsonStr));
        readFirstArray(reader);
        return mReturn;
    }

    private void readFirstArray(JsonReader reader) {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("title"))
                    mReturn[mCont++] = reader.nextString();
                else if(name.equals("description"))
                    mReturn[mCont++] = reader.nextString();
                else if(name.equals("primary_action"))
                    readAction(reader);
                else if(name.equals("secondary_action"))
                    readAction(reader);
                else if(name.equals("links"))
                    readLinks(reader);
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAction(JsonReader reader)
    {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("title"))
                    mReturn[mCont++] = reader.nextString();
                else if(name.equals("action"))
                    mReturn[mCont++] = reader.nextString();
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLinks(JsonReader reader)
    {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("chargeback"))
                    readHref(reader);
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHref(JsonReader reader)
    {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("href"))
                    mReturn[mCont++] = reader.nextString();
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
