package br.com.mfdonadeli.chargeback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mfdonadeli on 3/12/16.
 */
public class HttpRequest {
    public String doGetRequest(String sURL)
    {
        String response = "";
        try{
            URL url = new URL(sURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }
            in.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public String doPostRequest(String string) {
        return null;
    }

    public String sendPostRequest(String string, String string1) {
        return null;
    }
}
