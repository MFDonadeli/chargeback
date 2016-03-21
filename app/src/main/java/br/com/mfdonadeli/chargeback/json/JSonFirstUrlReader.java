package br.com.mfdonadeli.chargeback.json;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by mfdonadeli on 3/10/16.
 *
 * Class to parse the starter Json
 *
 * Json Sample:
 * <pre>   {@code
 *  {"links":{"notice":{"href":"https://nu-mobile-hiring.herokuapp.com/notice"}}}
 * } </pre>
 */
public class JSonFirstUrlReader {
    private final String JSON_LOG = "CHARGEBACK JSonFirst";
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
            Log.d(JSON_LOG, "readFirstArray. Mensagem: " + e.toString());
            mUrl = "--ERROR--";
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
            Log.d(JSON_LOG, "readLinks. Mensagem: " + e.toString());
            mUrl = "--ERROR--";
        }
    }

    private void readHref(JsonReader reader)
    {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("href"))
                    mUrl = reader.nextString();
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(JSON_LOG, "readHref. Mensagem: " + e.toString());
            mUrl = "--ERROR--";
        }
    }
}
