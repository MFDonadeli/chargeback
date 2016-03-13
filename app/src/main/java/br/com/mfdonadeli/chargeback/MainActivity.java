package br.com.mfdonadeli.chargeback;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
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

    private btnClick firstClickListener;
    private btnClick secondClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getFirstRequest();

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtDescription = (TextView) findViewById(R.id.txtDescription);

        mBtnPrimaryAction = (Button) findViewById(R.id.btnPrimaryAction);
        firstClickListener = new btnClick();
        mBtnPrimaryAction.setOnClickListener(firstClickListener);

        mBtnSecondaryAction = (Button) findViewById(R.id.btnSecondaryAction);
        secondClickListener = new btnClick();
        mBtnSecondaryAction.setOnClickListener(secondClickListener);
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
        firstClickListener.setAction(mPrimaryActionAction);
        mBtnSecondaryAction.setText(mSecondaryActionTitle);
        secondClickListener.setAction(mSecondaryActionAction);

        if(mPrimaryActionAction.equals("continue"))
        {
            mBtnPrimaryAction.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.enabled_font_size));
            mBtnPrimaryAction.setTextColor(ContextCompat.getColor(this, R.color.enabled_purple));
            mBtnSecondaryAction.setTextColor(ContextCompat.getColor(this, R.color.disabled_gray));
        }

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

        public void setAction(String sAction)
        {
            this.sAction = sAction;
        }

        @Override
        public void onClick(View view) {
            if(sAction.equals("continue"))
            {
                Intent intent = new Intent(MainActivity.this, ChargeBackActivity.class);
                intent.putExtra("URL", mContinueLink);
                startActivity(intent);
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

        @Override
        protected String doInBackground(String... strings) {
            HttpRequest request = new HttpRequest();

            if(strings[1].equals("first"))
                mStep = 0;
            else if(strings[1].equals("second"))
                mStep = 1;

            return request.doGetRequest(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if(mStep == 0) {
                JSonFirstUrlReader jr = new JSonFirstUrlReader();
                mLinkNotification = jr.getFirstURL(s);

                //Call the Notice URL to fill UI controls
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
