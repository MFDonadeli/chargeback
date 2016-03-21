package br.com.mfdonadeli.chargeback;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

        switchCard.setOnCheckedChangeListener(new switchChange());
        switchRecognized.setOnCheckedChangeListener(new switchChange());

        getRequest();

        txtDetails.setInputType(InputType.TYPE_CLASS_TEXT);
        txtDetails.setLines(10); // desired number of lines
        txtDetails.setHorizontallyScrolling(false);
        txtDetails.setImeOptions(EditorInfo.IME_ACTION_DONE);

        txtDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableContestButton();
            }
        });

    }

    /**
     * Enable Contest button (primary action), if details is not empty
     */
    private void enableContestButton() {
        btnPrimaryAction.setEnabled(txtDetails.getText().toString().trim().length() > 0);
        if(btnPrimaryAction.isEnabled())
            btnPrimaryAction.setTextColor(ContextCompat.getColor(this, R.color.enabled_purple));
        else
            btnPrimaryAction.setTextColor(ContextCompat.getColor(this, R.color.disabled_gray));
    }

    /**
     * Make request to fill UI
     */
    private void getRequest() {
        String[] params = new String[2];
        params[0] = mRequestUrl;
        params[1] = "get_content";
        new ExecRequest().execute(params);
    }

    /**
     * Fill UI Controls with strings
     */
    private void setContent() {
        txtDetails.setHint(Html.fromHtml(mCommentHint));
        txtTitle.setText(mTitle);
        switchRecognized.setText(mRecognizedTitle);
        switchCard.setText(mCardTitle);
        imgLock.setImageBitmap(setLockBitmap());

        btnPrimaryAction.setText(getResources().getString(R.string.ok_charge_back_btn));
        btnSecondaryAction.setText(getResources().getString(R.string.cancel_charge_back_btn));

        if(mAutoBlock) postToLock(true);
    }

    /**
     * Set Lock or Unlock bitmap. It depends if card is locked or not
     * @return Bitmap to be loaded in ImageView
     */
    private Bitmap setLockBitmap() {
        Bitmap bitmap, scaled;

        if(mCardBlocked) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_chargeback_lock);
            txtLock.setText(getResources().getString(R.string.block_card));
        }
        else
        {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_chargeback_unlock);
            txtLock.setText(getResources().getString(R.string.unblock_card));
        }

        scaled = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);

        return scaled;
    }

    /**
     * Call lock or unlock requests
     * @param b: Card will be locked: true/false
     */
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

    /**
     * If ChargeBack request is OK, call the dialog to show the confirmation to user
     */
    private void ChargeBackOk() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.chargeback_ok_dialog);

        final Button btnClose = (Button) dialog.findViewById(R.id.btnDialogClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    /**
     * Call method to update lock ImageView
     */
    private void setUIBlockCard() {
        imgLock.setImageBitmap(setLockBitmap());
    }

    /**
     * Create the JSon to be send and make the request to send it
     */
    private void sendChargeBack() {
        JSonChargeBackWriter js = new JSonChargeBackWriter();
        js.createObject();
        js.setString("comment", TextUtils.htmlEncode(txtDetails.getText().toString()));
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

        if(sText.equals("--ERROR--")){
            Toast.makeText(getApplicationContext(), R.string.error_chargeback, Toast.LENGTH_LONG).show();
        }
        else {
            String[] params = {mSelfUrl, "contest", sText};
            new ExecRequest().execute(params);
        }
    }


    /**
     * AsyncTask to perform HttpRequests, and call JsonParser after Http return results
     */
    private class ExecRequest extends AsyncTask<String, Void, String> {

        int mCont = 0;
        @Override
        protected String doInBackground(String... strings) {
            String sRet = "";
            switch (strings[1]) {
                case "get_content": {
                    HttpRequest request = new HttpRequest();
                    sRet = request.doGetRequest(strings[0]);
                    mCont = 1;
                    break;
                }
                case "block":
                case "unblock": {
                    HttpRequest request = new HttpRequest();
                    sRet = request.doPostRequest(strings[0]);
                    mCont = 2;
                    break;
                }
                case "contest": {
                    HttpRequest request = new HttpRequest();
                    sRet = request.sendJsonRequest(strings[0], strings[2]);
                    mCont = 3;
                    break;
                }
            }
            return sRet;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("--ERROR--") || s.trim().isEmpty()){
                Toast.makeText(getApplicationContext(), R.string.error_internet, Toast.LENGTH_LONG).show();
                //End if cannot get String from ChargeBack Json
                if(mCont == 1) finish();
                return;
            }

            if(mCont == 1) {
                JSonChargeBackUrlReader jr = new JSonChargeBackUrlReader();
                String[] mReturn = jr.getReturn(s);

                if(mReturn == null)
                {
                    Toast.makeText(getApplicationContext(), R.string.error_internet, Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    mCommentHint = mReturn[0];
                    mTitle = mReturn[1];
                    mAutoBlock = mReturn[2].equals("true");
                    mReasonId = mReturn[3];
                    mRecognizedTitle = mReturn[4];
                    mReasonResponse = mReturn[5];
                    mCardTitle = mReturn[6];
                    mBlockUrl = mReturn[7];
                    mUnblockUrl = mReturn[8];
                    mSelfUrl = mReturn[9];

                    //Fill UI controls
                    setContent();
                }
            }
            else if(mCont == 2)
            {
                JSonStatusReader jsonReader = new JSonStatusReader();
                if(jsonReader.isOK(s)) {
                    mCardBlocked = !mCardBlocked;
                    setUIBlockCard();
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.error_block, Toast.LENGTH_LONG).show();
                }
            }
            else if(mCont == 3)
            {
                JSonStatusReader jsonReader = new JSonStatusReader();
                if(jsonReader.isOK(s)) {
                    ChargeBackOk();
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.error_chargeback, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Click listeners for two button and ImageView (lock or unlock card)
     */
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
                postToLock(!mCardBlocked);
            }
        }
    }

    /**
     * Change color of switch controls
     */
    private class switchChange implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b)
                compoundButton.setTextColor(ContextCompat.getColor(ChargeBackActivity.this, R.color.green));
            else
                compoundButton.setTextColor(ContextCompat.getColor(ChargeBackActivity.this, R.color.texts));
        }
    }
}
