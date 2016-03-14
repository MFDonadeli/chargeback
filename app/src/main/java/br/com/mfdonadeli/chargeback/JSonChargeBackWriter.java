package br.com.mfdonadeli.chargeback;

import android.util.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

/**
 * Created by mfdonadeli on 3/13/16.
 */
public class JSonChargeBackWriter {
    StringWriter strWriter;
    JsonWriter writer;

    public JSonChargeBackWriter(){
        strWriter = new StringWriter();
        writer = new JsonWriter(strWriter);
    }

    public String getJsonText()
    {
        return String.valueOf(strWriter.toString());
    }

    public void createObject() {
        try {
            writer.beginObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createArray() {
        try {
            writer.beginArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setString(String value, String text)
    {
        try {
            writer.name(value).value(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setString(String value, boolean b)
    {
        try {
            writer.name(value).value(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setString(String value) {
        try {
            writer.name(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endObject() {
        try {
            writer.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endArray() {
        try {
            writer.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
