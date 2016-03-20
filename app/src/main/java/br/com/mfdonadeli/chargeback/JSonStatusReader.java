package br.com.mfdonadeli.chargeback;

import android.util.JsonReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by mfdonadeli on 3/19/16.
 */
public class JSonStatusReader {
    public boolean isOK(String sJsonStr){
        JsonReader reader = new JsonReader(new StringReader(sJsonStr));
        return readFirstArray(reader);
    }

    private boolean readFirstArray(JsonReader reader) {
        String sRet = "";
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("status"))
                    sRet = reader.nextString();
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(sRet.equals("Ok"))
            return true;

        return false;
    }
}
