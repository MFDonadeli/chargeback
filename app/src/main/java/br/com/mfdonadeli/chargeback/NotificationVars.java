package br.com.mfdonadeli.chargeback;

/**
 * Created by mfdonadeli on 3/20/16.
 * Class that contains the UI information for MainActivity (Notification Screen)
 */
public class NotificationVars {
    private String mTitle;
    private String mDescription;

    private String mPrimaryActionTitle;
    private String mPrimaryActionAction;

    private String mSecondaryActionTitle;
    private String mSecondaryActionAction;

    private String mContinueLink;

    private boolean mHasError = false;

    public boolean hasError() { return mHasError; }
    public void setError() { mHasError = true; }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getPrimaryActionTitle() {
        return mPrimaryActionTitle;
    }

    public void setPrimaryActionTitle(String mPrimaryActionTitle) {
        this.mPrimaryActionTitle = mPrimaryActionTitle;
    }

    public String getPrimaryActionAction() {
        return mPrimaryActionAction;
    }

    public void setPrimaryActionAction(String mPrimaryActionAction) {
        this.mPrimaryActionAction = mPrimaryActionAction;
    }

    public String getSecondaryActionTitle() {
        return mSecondaryActionTitle;
    }

    public void setSecondaryActionTitle(String mSecondaryActionTitle) {
        this.mSecondaryActionTitle = mSecondaryActionTitle;
    }

    public String getSecondaryActionAction() {
        return mSecondaryActionAction;
    }

    public void setSecondaryActionAction(String mSecondaryActionAction) {
        this.mSecondaryActionAction = mSecondaryActionAction;
    }

    public String getContinueLink() {
        return mContinueLink;
    }

    public void setContinueLink(String mContinueLink) {
        this.mContinueLink = mContinueLink;
    }
}
