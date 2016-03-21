package br.com.mfdonadeli.chargeback;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by mfdonadeli on 3/12/16.
 *
 * Class to parse Json Notice URL for fill UI controls for the ChargeBack screen (ChargeBackActivity)
 * Json Sample:
 * <pre>   {@code
 *  {"comment_hint":"Nos conte <strong>em detalhes</strong> o que aconteceu com a sua compra em Transaction...",
 *  "id":"fraud",
 *  "title":"Não reconheço esta compra",
 *  "autoblock":true,
 *  "reason_details":
 *  [{"id":"merchant_recognized",
 *    "title":"Reconhece o estabelecimento?"},
 *    {"id":"card_in_possession","title":"Está com o cartão em mãos?"}],
 *  "links":{"block_card":{"href":"https://nu-mobile-hiring.herokuapp.com/card_block"},
 *  "unblock_card":{"href":"https://nu-mobile-hiring.herokuapp.com/card_unblock"},
 *  "self":{"href":"https://nu-mobile-hiring.herokuapp.com/chargeback"}}}
 * }</pre>
 */
public class JSonChargeBackUrlReader {
    final String JSON_LOG = "CHARGEBACK JSonCB";
    private String[] mReturn;
    int mCont = 0;
    boolean bError = false;
    public String[] getReturn(String sJsonStr){
        mReturn = new String[10];
        JsonReader reader = new JsonReader(new StringReader(sJsonStr));
        readFirstArray(reader);

        if(bError) mReturn = null;

        return mReturn;
    }

    private void readFirstArray(JsonReader reader) {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "comment_hint":
                        mReturn[mCont++] = reader.nextString();
                        break;
                    case "id":
                        reader.skipValue();//nothing
                        break;
                    case "title":
                        mReturn[mCont++] = reader.nextString();
                        break;
                    case "autoblock":
                        mReturn[mCont++] = String.valueOf(reader.nextBoolean());
                        break;
                    case "reason_details":
                        readReasonDetails(reader);
                        break;
                    case "links":
                        readLinks(reader);
                        break;
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(JSON_LOG, "readFirstArray. Mensagem: " + e.toString());
            bError = true;
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
            Log.d(JSON_LOG, "readReasonDetails. Mensagem: " + e.toString());
            bError = true;
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
            Log.d(JSON_LOG, "readLink. Mensagem: " + e.toString());
            bError = true;
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
            Log.d(JSON_LOG, "readHref. Mensagem: " + e.toString());
            bError = true;
        }
    }
}
