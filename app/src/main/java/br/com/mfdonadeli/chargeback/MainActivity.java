package br.com.mfdonadeli.chargeback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.mfdonadeli.chargeback.http.HttpRequest;
import br.com.mfdonadeli.chargeback.json.JSonFirstUrlReader;
import br.com.mfdonadeli.chargeback.json.JSonNoticeUrlReader;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private static final String BASEURL = "https://nu-mobile-hiring.herokuapp.com/";
    private static int FIRST_REQUEST = 0;
    private static int NOTICE_REQUEST = 1;

    private String mLinkNotification;
    private NotificationVars notificationVars;

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

    /**
     * Call the firt URL Request. This method will call the only hardcoded URL
     */
    private void getFirstRequest() {
        String[] params = new String[2];
        params[0] = BASEURL;
        params[1] = "first";
        new ExecRequest().execute(params);
    }

    /**
     * Fill UI Controls with strings
     */
    private void setContents()
    {
        txtTitle.setText(Html.fromHtml(notificationVars.getTitle()));
        txtDescription.setText(Html.fromHtml(notificationVars.getDescription()));
        mBtnPrimaryAction.setText(notificationVars.getPrimaryActionTitle());
        firstClickListener.setAction(notificationVars.getPrimaryActionAction());
        mBtnSecondaryAction.setText(notificationVars.getSecondaryActionTitle());
        secondClickListener.setAction(notificationVars.getSecondaryActionAction());

        if(notificationVars.getPrimaryActionAction().equals("continue"))
        {
            mBtnPrimaryAction.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.enabled_font_size));
            mBtnPrimaryAction.setTextColor(ContextCompat.getColor(this, R.color.enabled_purple));
            mBtnSecondaryAction.setTextColor(ContextCompat.getColor(this, R.color.close_gray));
        }

    }

    /**
     * Call Notice URL Request to fill the UI and get next URLs
     */
    private void getNoticeRequest(){
        String[] params = new String[2];
        params[0] = mLinkNotification;
        params[1] = "second";
        new ExecRequest().execute(params);
    }

    private void showRetryDialog()
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.create();
        dialog.setTitle(R.string.error_internet_retry);
        dialog.setPositiveButton("Tentar Novamente", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getFirstRequest();
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        dialog.show();
    }

    /**
     * Click Listener class for both buttons
     */
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
                intent.putExtra("URL", notificationVars.getContinueLink());
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
                mStep = FIRST_REQUEST;
            else if(strings[1].equals("second"))
                mStep = NOTICE_REQUEST;

            return request.doGetRequest(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if(mStep == FIRST_REQUEST) {
                JSonFirstUrlReader jr = new JSonFirstUrlReader();
                mLinkNotification = jr.getFirstURL(s);

                if(mLinkNotification.equals("--ERROR--")
                        || mLinkNotification.trim().isEmpty())
                {
                    showRetryDialog();
                }
                else {

                    //Call the Notice URL to fill UI controls
                    getNoticeRequest();
                }
            }
            else if(mStep == NOTICE_REQUEST)
            {
                JSonNoticeUrlReader jr = new JSonNoticeUrlReader();
                notificationVars = jr.getReturn(s);

                if(notificationVars.hasError()){
                    showRetryDialog();
                }
                else {
                    //fill UI controls
                    setContents();
                }
            }
        }
    }
}
