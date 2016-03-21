package br.com.mfdonadeli.chargeback;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by mfdonadeli on 3/19/16.
 *
 * Class to parse Json Status (after action posts (block, unblock and chargeback)
 * Json Sample:
 * <pre>   {@code
 *  {"status":"Ok"}
 * }</pre>
 */
public class JSonStatusReader {
    final String JSON_LOG = "CHARGEBACK JSonStatus";
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

            if(sRet.equals("Ok")) {
                return true;
            }

        } catch (IOException e) {
            Log.d(JSON_LOG, "readFirstArray. Mensagem: " + e.toString());
            e.printStackTrace();
        }

        return false;
    }
}
