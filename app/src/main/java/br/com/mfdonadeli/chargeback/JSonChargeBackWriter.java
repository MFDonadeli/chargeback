package br.com.mfdonadeli.chargeback;

import android.util.JsonWriter;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

/**
 * Created by mfdonadeli on 3/13/16.
 *
 * Create Json to ChargeBack the transaction
 *
 * Json Sample: <pre>{@code
 * {"comment":"Estava viajando...",
 *  "reason_details": [
 *  {"id":"merchant_recognized",
 *   "response":false},
 *  {"id":"card_in_possession",
 *   "response":true}
 *  ]}
 * }</pre>
 */
public class JSonChargeBackWriter {
    final String JSON_LOG = "CHARGEBACK JsonWrite";
    StringWriter strWriter;
    JsonWriter writer;
    boolean bError = false;

    public JSonChargeBackWriter(){
        strWriter = new StringWriter();
        writer = new JsonWriter(strWriter);
    }

    public String getJsonText()
    {
        String sRet;
        if(bError)
            sRet = "--ERROR--";
        else
            sRet = String.valueOf(strWriter.toString());

        return sRet;
    }

    public void createObject() {
        try {
            writer.beginObject();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(JSON_LOG, "createObject. Mensagem: " + e.toString());
            bError = true;
        }
    }

    public void createArray() {
        try {
            writer.beginArray();
        } catch (IOException e) {
            Log.d(JSON_LOG, "createArray. Mensagem: " + e.toString());
            e.printStackTrace();
            bError = true;
        }
    }

    public void setString(String value, String text)
    {
        try {
            writer.name(value).value(text);
        } catch (IOException e) {
            Log.d(JSON_LOG, "setString(s,s). Mensagem: " + e.toString());
            e.printStackTrace();
            bError = true;
        }
    }

    public void setString(String value, boolean b)
    {
        try {
            writer.name(value).value(b);
        } catch (IOException e) {
            Log.d(JSON_LOG, "setString(s,b). Mensagem: " + e.toString());
            e.printStackTrace();
            bError = true;
        }
    }

    public void setString(String value) {
        try {
            writer.name(value);
        } catch (IOException e) {
            Log.d(JSON_LOG, "setString(s). Mensagem: " + e.toString());
            e.printStackTrace();
            bError = true;
        }
    }

    public void endObject() {
        try {
            writer.endObject();
        } catch (IOException e) {
            Log.d(JSON_LOG, "endObject. Mensagem: " + e.toString());
            e.printStackTrace();
            bError = true;
        }
    }

    public void endArray() {
        try {
            writer.endArray();
        } catch (IOException e) {
            Log.d(JSON_LOG, "endArray. Mensagem: " + e.toString());
            e.printStackTrace();
            bError = true;
        }
    }
}
