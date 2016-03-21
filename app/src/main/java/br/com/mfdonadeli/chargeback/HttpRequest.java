package br.com.mfdonadeli.chargeback;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by mfdonadeli on 3/12/16.
 * Class to do all HTTP requests Post, Get and SendJson (ChargeBack)
 */
public class HttpRequest {
    final String HTTP_REQUEST_LOG = "CHARGEBACK HttpRequest";

    /**
     * Do a Get Request to sURL
     * @param sURL: URL
     * @return Response of the request, or --ERROR-- on Exception
     */
    public String doGetRequest(String sURL) {
        String response = "";
        try {
            URL url = new URL(sURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "doGetRequest. Malformed URL. Message: " + e.toString());
            response = "--ERROR--";
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "doGetRequest. IOExcepetion. Message: " + e.toString());
            response = "--ERROR--";
        }

        return response;
    }

    /**
     * Do a Post Request to sURL
     * @param sURL: URL
     * @return Response of the request, or --ERROR-- on Exception
     */
    public String doPostRequest(String sURL) {
        String response = "";
        StringBuilder post = new StringBuilder();
        URL url;
        try {
            url = new URL(sURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "doPostRequest. Malformed URL. Message: " + e.toString());
            response = "--ERROR--";
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "doPostRequest. ProtocolException. Message: " + e.toString());
            response = "--ERROR--";
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "doPostRequest. IOException. Message: " + e.toString());
            response = "--ERROR--";
        }

        return response;

    }

    /**
     * Send Json to URL
     * @param sURL: URL
     * @param sJson: Json String to send
     * @return Response of the request, or --ERROR-- on Exception
     */
    public String sendJsonRequest(String sURL, String sJson) {
        String response = "";
        try {
            URL url = new URL(sURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream os = conn.getOutputStream();
            os.write(sJson.getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "sendJsonRequest. Malformed URL. Message: " + e.toString());
            response = "--ERROR--";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "sendJsonRequest. UnsupportedEncodingException. Message: " + e.toString());
            response = "--ERROR--";
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "sendJsonRequest. ProtocolException. Message: " + e.toString());
            response = "--ERROR--";
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(HTTP_REQUEST_LOG, "sendJsonRequest. IOException. Message: " + e.toString());
            response = "--ERROR--";
        }

        return response;
    }
}
