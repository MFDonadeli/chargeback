package br.com.mfdonadeli.chargeback.json;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import br.com.mfdonadeli.chargeback.ChargeBackVars;

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
    private final String JSON_LOG = "CHARGEBACK JSonCB";
    private ChargeBackVars mReturn;

    public ChargeBackVars getReturn(String sJsonStr){
        mReturn = new ChargeBackVars();
        JsonReader reader = new JsonReader(new StringReader(sJsonStr));
        readFirstArray(reader);

        return mReturn;
    }

    private void readFirstArray(JsonReader reader) {
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "comment_hint":
                        mReturn.setCommentHint(reader.nextString());
                        break;
                    case "id":
                        mReturn.setId(reader.nextString());
                        break;
                    case "title":
                        mReturn.setTitle(reader.nextString());
                        break;
                    case "autoblock":
                        mReturn.setAutoBlock(reader.nextBoolean());
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
            mReturn.setError();
        }

    }

    private void readReasonDetails(JsonReader reader) {
        String sId, sTitle;
        sId = sTitle = "";

        try {
            reader.beginArray();
            while(reader.hasNext()) {
                reader.beginObject();
                while(reader.hasNext())
                {
                    String name = reader.nextName();
                    if(name.equals("id"))
                        sId = reader.nextString();
                    else if(name.equals("title"))
                        sTitle = reader.nextString();
                }
                reader.endObject();
                mReturn.setReasonDetail(sId, sTitle);
            }
            reader.endArray();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(JSON_LOG, "readReasonDetails. Mensagem: " + e.toString());
            mReturn.setError();
        }
    }

    private void readLinks(JsonReader reader) {
        try {
            reader.beginObject();
            while(reader.hasNext())
            {
                String name = reader.nextName();
                switch (name) {
                    case "block_card":
                        mReturn.setBlockUrl(readHref(reader));
                        break;
                    case "unblock_card":
                        mReturn.setUnblockUrl(readHref(reader));
                        break;
                    case "self":
                        mReturn.setSelfUrl(readHref(reader));
                        break;
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(JSON_LOG, "readLink. Mensagem: " + e.toString());
            mReturn.setError();
        }
    }

    private String readHref(JsonReader reader) {
        String sRet = "";
        try {
            reader.beginObject();
            while(reader.hasNext())
            {
                String name = reader.nextName();
                if(name.equals("href"))
                {
                    sRet = reader.nextString();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(JSON_LOG, "readHref. Mensagem: " + e.toString());
            mReturn.setError();
        }
        return sRet;
    }
}
