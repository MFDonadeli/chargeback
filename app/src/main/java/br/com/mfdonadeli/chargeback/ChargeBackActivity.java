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

import br.com.mfdonadeli.chargeback.http.HttpRequest;
import br.com.mfdonadeli.chargeback.json.JSonChargeBackUrlReader;
import br.com.mfdonadeli.chargeback.json.JSonChargeBackWriter;
import br.com.mfdonadeli.chargeback.json.JSonStatusReader;

public class ChargeBackActivity extends AppCompatActivity {

    private final int CHARGEBACK_REQUEST = 1;
    private final int CHARGEBACK_BLOCK_UNBLOCK_CARD_REQUEST = 2;
    private final int CHARGEBACK_SEND_CONTEST = 3;

    private ChargeBackVars chargeBackVars;
    private String mRequestUrl;

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
        txtDetails.setHint(Html.fromHtml(chargeBackVars.getCommentHint()));
        txtTitle.setText(chargeBackVars.getTitle());
        switchRecognized.setText(chargeBackVars.getReasonTitle(0));
        switchCard.setText(chargeBackVars.getReasonTitle(1));
        imgLock.setImageBitmap(setLockBitmap());

        btnPrimaryAction.setText(getResources().getString(R.string.ok_charge_back_btn));
        btnSecondaryAction.setText(getResources().getString(R.string.cancel_charge_back_btn));

        if(chargeBackVars.isAutoBlock()) postToLock(true);
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
            params[0] = chargeBackVars.getBlockUrl();
            params[1] = "block";
            new ExecRequest().execute(params);
        }
        else
        {
            //Try to unlock card
            String[] params = new String[2];
            params[0] = chargeBackVars.getUnblockUrl();
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
        js.createArray(chargeBackVars.getReasonDetails());
        js.endArray();
        js.endObject();

        String sText = js.getJsonText();

        if(sText.equals("--ERROR--")){
            Toast.makeText(getApplicationContext(), R.string.error_chargeback, Toast.LENGTH_LONG).show();
        }
        else {
            String[] params = {chargeBackVars.getSelfUrl(), "contest", sText};
            new ExecRequest().execute(params);
        }
    }


    /**
     * AsyncTask to perform HttpRequests, and call JsonParser after Http return results
     */
    private class ExecRequest extends AsyncTask<String, Void, String> {

        int mStep = 0;
        @Override
        protected String doInBackground(String... strings) {
            String sRet = "";
            switch (strings[1]) {
                case "get_content": {
                    HttpRequest request = new HttpRequest();
                    sRet = request.doGetRequest(strings[0]);
                    mStep = CHARGEBACK_REQUEST;
                    break;
                }
                case "block":
                case "unblock": {
                    HttpRequest request = new HttpRequest();
                    sRet = request.doPostRequest(strings[0]);
                    mStep = CHARGEBACK_BLOCK_UNBLOCK_CARD_REQUEST;
                    break;
                }
                case "contest": {
                    HttpRequest request = new HttpRequest();
                    sRet = request.sendJsonRequest(strings[0], strings[2]);
                    mStep = CHARGEBACK_SEND_CONTEST;
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
                if(mStep == CHARGEBACK_REQUEST) finish();
                return;
            }

            if(mStep == CHARGEBACK_REQUEST) {
                JSonChargeBackUrlReader jr = new JSonChargeBackUrlReader();
                chargeBackVars = jr.getReturn(s);

                if(chargeBackVars.hasError())
                {
                    Toast.makeText(getApplicationContext(), R.string.error_internet, Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    //Fill UI controls
                    setContent();
                }
            }
            else if(mStep == CHARGEBACK_BLOCK_UNBLOCK_CARD_REQUEST)
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
            else if(mStep == CHARGEBACK_SEND_CONTEST)
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

            if(compoundButton.getId() == R.id.switchCBmerchant_recognized)
                chargeBackVars.setReasonValue(0, b);
            else if(compoundButton.getId() == R.id.switchCBcard_in_possession)
                chargeBackVars.setReasonValue(1, b);
        }
    }
}
