package br.com.mfdonadeli.chargeback;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by mfdonadeli on 3/11/16.
 *
 * Class to parse Json Notice URL for fill UI controls for the Notice screen (Main Activity)
 * Json Sample:
 * <pre>   {@code
 *  {"title":"Antes de continuar",
 *   "description":"<p>Estamos com você nesta! Certifique-se dos pontos abaixo,
 *  são muito importantes:<br/><strong>• Você pode <font color=\"#6e2b77\">procurar o nome do estabelecimento
 *  no Google</font>. Diversas vezes encontramos informações valiosas por lá e elas podem te ajudar neste processo.</strong><br/>
 *  <strong>• Caso você reconheça a compra, é muito importante pra nós que entre em contato com o estabelecimento e
 *  certifique-se que a situação já não foi resolvida.</strong></p>",
 *  "primary_action":{"title":"Continuar","action":"continue"},
 *  "secondary_action":{"title":"Fechar","action":"cancel"},
 *  "links":{"chargeback":{"href":"https://nu-mobile-hiring.herokuapp.com/chargeback"}}}
 * }</pre>
 *
 */
public class JSonNoticeUrlReader {
    final String JSON_LOG = "CHARGEBACK JSonNotice";
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
                switch (name) {
                    case "title":
                        mReturn[mCont++] = reader.nextString();
                        break;
                    case "description":
                        mReturn[mCont++] = reader.nextString();
                        break;
                    case "primary_action":
                        readAction(reader);
                        break;
                    case "secondary_action":
                        readAction(reader);
                        break;
                    case "links":
                        readLinks(reader);
                        break;
                }
            }
            reader.endObject();
        } catch (IOException e) {
            Log.d(JSON_LOG, "readFirstArray. Mensagem: " + e.toString());
            e.printStackTrace();
            mReturn = null;
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
            Log.d(JSON_LOG, "readAction. Mensagem: " + e.toString());
            e.printStackTrace();
            mReturn = null;
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
            Log.d(JSON_LOG, "readLinks. Mensagem: " + e.toString());
            e.printStackTrace();
            mReturn = null;
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
            Log.d(JSON_LOG, "readHref. Mensagem: " + e.toString());
            e.printStackTrace();
            mReturn = null;
        }
    }
}
