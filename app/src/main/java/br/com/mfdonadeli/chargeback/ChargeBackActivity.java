package br.com.mfdonadeli.chargeback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ChargeBackActivity extends AppCompatActivity {

    private String mRequestUrl;

    private String mCommentHint;
    private String mTitle;
    private boolean mAutoBlock;
    private String mRecognizedTitle;
    private String mCardTitle;
    private String mBlockUrl;
    private String mUnblockUrl;
    private String mSelfUrl;
    private String mReasonId;
    private String mReasonResponse;

    private TextView txtTitle;
    private ImageView imgLock;
    private TextView txtLock;
    private Switch switchRecognized;
    private Switch switchCard;
    private EditText txtDetails;
    private Button btnPrimaryAction;
    private Button btnSecondaryAction;

    private boolean mCardBlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_back);

        Bundle bundle = getIntent().getExtras();

        if(bundle.getString("URL") != null)
        {
            mRequestUrl = bundle.getString("URL");
        }

        txtTitle = (TextView) findViewById(R.id.txtCBTitle);
        imgLock = (ImageView) findViewById(R.id.imgCBLock);
        txtLock = (TextView) findViewById(R.id.txtCBLock);
        switchRecognized = (Switch) findViewById(R.id.switchCBmerchant_recognized);
        switchCard = (Switch) findViewById(R.id.switchCBcard_in_possession);
        txtDetails = (EditText) findViewById(R.id.edtCBDetails);
        btnPrimaryAction = (Button) findViewById(R.id.btnCBPrimaryAction);
        btnSecondaryAction = (Button) findViewById(R.id.btnCBSecondaryAction);

        imgLock.setOnClickListener(new btnClick());
        btnPrimaryAction.setOnClickListener(new btnClick());
        btnSecondaryAction.setOnClickListener(new btnClick());

        getRequest();
    }

    private void getRequest() {
        String[] params = new String[2];
        params[0] = mRequestUrl;
        params[1] = "get_content";
        new ExecRequest().execute(params);
    }

    private void setContent() {
        txtDetails.setHint(Html.fromHtml(mCommentHint));
        txtTitle.setText(mTitle);
        switchRecognized.setText(mRecognizedTitle);
        switchCard.setText(mCardTitle);
        txtLock.setText(getResources().getString(R.string.unblock_card));
        imgLock.setImageBitmap(setLockBitmap());

        btnPrimaryAction.setText(getResources().getString(R.string.ok_charge_back_btn));
        btnSecondaryAction.setText(getResources().getString(R.string.cancel_charge_back_btn));
    }

    private Bitmap setLockBitmap() {
        Bitmap bitmap, scaled;
        bitmap = null;

        if(mCardBlocked) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_chargeback_lock);
            postToLock(true);
        }
        else
        {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_chargeback_unlock);
            postToLock(false);
        }

        scaled = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);

        return scaled;
    }

    private void postToLock(boolean b) {
        if(b)
        {
            //Try to lock card
            String[] params = new String[2];
            params[0] = mBlockUrl;
            params[1] = "block";
            new ExecRequest().execute(params);
        }
        else
        {
            //Try to unlock card
            String[] params = new String[2];
            params[0] = mUnblockUrl;
            params[1] = "unblock";
            new ExecRequest().execute(params);
        }
    }

    private class ExecRequest extends AsyncTask<String, Void, String> {

        int mCont = 0;
        @Override
        protected String doInBackground(String... strings) {
            String sRet = "";
            if(strings[1].equals("get_content")) {
                HttpRequest request = new HttpRequest();
                sRet = request.doGetRequest(strings[0]);
                mCont = 1;
            }
            else if(strings[1].equals("block") || strings[1].equals("unblock")){
                HttpRequest request = new HttpRequest();
                sRet = request.doPostRequest(strings[0]);
                mCont = 2;
            }
            else if(strings[1].equals("contest"))
            {
                HttpRequest request = new HttpRequest();
                sRet = request.sendPostRequest(strings[0], strings[2]);
                mCont = 3;
            }
            return sRet;
        }

        @Override
        protected void onPostExecute(String s) {
            if(mCont == 1) {
                JSonChargeBackUrlReader jr = new JSonChargeBackUrlReader();
                String[] mReturn = jr.getReturn(s);

                mCommentHint = mReturn[0];
                mTitle = mReturn[1];
                mAutoBlock = mReturn[2].equals("true") ? true : false;
                mReasonId = mReturn[3];
                mRecognizedTitle = mReturn[4];
                mReasonResponse = mReturn[5];
                mCardTitle = mReturn[6];
                mBlockUrl = mReturn[7];
                mUnblockUrl = mReturn[8];
                mSelfUrl = mReturn[9];

                setContent();
            }
            else if(mCont == 3)
            {

            }
        }
    }

    private class btnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnCBPrimaryAction)
            {
                sendChargeBack();
            }
            else if(view.getId() == R.id.btnCBSecondaryAction)
            {
                finish();
            }
            else if(view.getId() == R.id.imgCBLock)
            {

            }
        }
    }

    private void sendChargeBack() {
        JSonChargeBackWriter js = new JSonChargeBackWriter();
        js.createObject();
        js.setString("comment", txtDetails.getText().toString());
        js.setString("reason_details");
        js.createArray();
        js.createObject();
        js.setString("id", mReasonId);
        js.setString("response", switchRecognized.isChecked());
        js.endObject();
        js.createObject();
        js.setString("id", mReasonResponse);
        js.setString("response", switchCard.isChecked());
        js.endObject();
        js.endArray();
        js.endObject();

        String sText = js.getJsonText();
        sText = "";

    }
}
