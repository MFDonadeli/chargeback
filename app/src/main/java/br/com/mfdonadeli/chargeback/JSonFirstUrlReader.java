package br.com.mfdonadeli.chargeback;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by mfdonadeli on 3/10/16.
 */
public class JSonFirstUrlReader {
    private String mUrl;
    public String getFirstURL(String sJsonStr)
    {
        JsonReader reader = new JsonReader(new StringReader(sJsonStr));
        readFirstArray(reader);

        return mUrl;
    }

    private void readFirstArray(JsonReader reader)
    {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("links"))
                    readLinks(reader);
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
                if(name.equals("notice"))
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
                    mUrl = reader.nextString();
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
