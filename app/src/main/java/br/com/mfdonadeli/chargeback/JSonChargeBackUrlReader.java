package br.com.mfdonadeli.chargeback;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by mfdonadeli on 3/12/16.
 */
public class JSonChargeBackUrlReader {
    private String[] mReturn;
    int mCont = 0;
    public String[] getReturn(String sJsonStr){
        mReturn = new String[10];
        JsonReader reader = new JsonReader(new StringReader(sJsonStr));
        readFirstArray(reader);
        return mReturn;
    }

    private void readFirstArray(JsonReader reader) {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("comment_hint"))
                    mReturn[mCont++] = reader.nextString();
                else if(name.equals("id"))
                    reader.skipValue();//nothing
                else if(name.equals("title"))
                    mReturn[mCont++] = reader.nextString();
                else if(name.equals("autoblock"))
                    mReturn[mCont++] = String.valueOf(reader.nextBoolean());
                else if(name.equals("reason_details"))
                    readReasonDetails(reader);
                else if(name.equals("links"))
                    readLinks(reader);
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readReasonDetails(JsonReader reader) {
        try {
            reader.beginArray();
            while(reader.hasNext()) {
                reader.beginObject();
                while(reader.hasNext())
                {
                    String name = reader.nextName();
                    if(name.equals("id"))
                        mReturn[mCont++] = reader.nextString();
                    else if(name.equals("title"))
                        mReturn[mCont++] = reader.nextString();
                }
                reader.endObject();
            }
            reader.endArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLinks(JsonReader reader) {
        try {
            reader.beginObject();
            while(reader.hasNext())
            {
                String name = reader.nextName();
                if(name.equals("block_card") || name.equals("unblock_card") || name.equals("self"))
                {
                    readHref(reader);
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHref(JsonReader reader) {
        try {
            reader.beginObject();
            while(reader.hasNext())
            {
                String name = reader.nextName();
                if(name.equals("href"))
                {
                    mReturn[mCont++] = reader.nextString();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
