package br.com.mfdonadeli.chargeback;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements Constants {

    private String mLinkNotification;
    private String mTitle;
    private String mDescription;

    private String mPrimaryActionTitle;
    private String mPrimaryActionAction;

    private String mSecondaryActionTitle;
    private String mSecondaryActionAction;

    private String mContinueLink;

    private TextView txtTitle;
    private TextView txtDescription;
    private Button mBtnPrimaryAction;
    private Button mBtnSecondaryAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getFirstRequest();

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);

        mBtnPrimaryAction = (Button) findViewById(R.id.btnPrimaryAction);
        mBtnPrimaryAction.setOnClickListener(new btnClick(mPrimaryActionAction));

        mBtnSecondaryAction = (Button) findViewById(R.id.btnSecondaryAction);
        mBtnSecondaryAction.setOnClickListener(new btnClick(mSecondaryActionAction));
    }

    private void getFirstRequest() {
        String[] params = new String[2];
        params[0] = BASEURL;
        params[1] = "first";
        new ExecRequest().execute(params);
    }

    private void setContents()
    {
        txtTitle.setText(Html.fromHtml(mTitle));
        txtDescription.setText(Html.fromHtml(mDescription));
        mBtnPrimaryAction.setText(mPrimaryActionTitle);
        mBtnSecondaryAction.setText(mSecondaryActionTitle);
    }

    private void getNoticeRequest(){
        String[] params = new String[2];
        params[0] = mLinkNotification;
        params[1] = "second";
        new ExecRequest().execute(params);
    }

    private class btnClick implements View.OnClickListener
    {
        String sAction;
        public btnClick(String sAction)
        {
            this.sAction = sAction;
        }

        @Override
        public void onClick(View view) {
            if(sAction.equals("continue"))
            {
                startActivity(new Intent(MainActivity.this, ChargeBackActivity.class));
            }
            else if(sAction.equals("cancel"))
            {
                finish();
            }
        }
    }

    /**
     * AsyncTask to perform HttpRequests, and call JsonParser after Http return results
     */
    private class ExecRequest extends AsyncTask<String, Void, String>
    {
        int mStep;
        private String doGetRequest(String sURL)
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

        @Override
        protected String doInBackground(String... strings) {
            if(strings[1].equals("first"))
                mStep = 0;
            else if(strings[1].equals("second"))
                mStep = 1;

            return doGetRequest(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if(mStep == 0) {
                JSonFirstUrlReader jr = new JSonFirstUrlReader();
                mLinkNotification = jr.getFirstURL(s);
                getNoticeRequest();
            }
            else if(mStep == 1)
            {
                JSonNoticeUrlReader jr = new JSonNoticeUrlReader();
                String[] mReturn = jr.getReturn(s);

                mTitle = mReturn[0];
                mDescription = mReturn[1];
                mPrimaryActionTitle = mReturn[2];
                mPrimaryActionAction = mReturn[3];
                mSecondaryActionTitle = mReturn[4];
                mSecondaryActionAction = mReturn[5];
                mContinueLink = mReturn[6];

                setContents();
            }
        }
    }
}
