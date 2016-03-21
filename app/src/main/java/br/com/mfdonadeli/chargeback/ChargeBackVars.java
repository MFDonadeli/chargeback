package br.com.mfdonadeli.chargeback;

import java.util.ArrayList;


/**
 * Created by mfdonadeli on 3/21/16.
 * Class that contains the UI information for ChangeBackActivity
 */
public class ChargeBackVars {
    private String mCommentHint;
    private String mTitle;
    private boolean mAutoBlock;
    private String mBlockUrl;
    private String mUnblockUrl;
    private String mSelfUrl;
    private String mId;
    private ArrayList<ReasonDetails> mReasonDetails;

    private boolean mHasError = false;

    public ChargeBackVars()
    {
        mReasonDetails = new ArrayList<>();
    }

    public boolean hasError() { return mHasError; }
    public void setError() { mHasError = true; }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getCommentHint() {
        return mCommentHint;
    }

    public void setCommentHint(String mCommentHint) {
        this.mCommentHint = mCommentHint;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public boolean isAutoBlock() {
        return mAutoBlock;
    }

    public void setAutoBlock(boolean mAutoBlock) {
        this.mAutoBlock = mAutoBlock;
    }

    public String getBlockUrl() {
        return mBlockUrl;
    }

    public void setBlockUrl(String mBlockUrl) {
        this.mBlockUrl = mBlockUrl;
    }

    public String getUnblockUrl() {
        return mUnblockUrl;
    }

    public void setUnblockUrl(String mUnblockUrl) {
        this.mUnblockUrl = mUnblockUrl;
    }

    public String getSelfUrl() {
        return mSelfUrl;
    }

    public void setSelfUrl(String mSelfUrl) {
        this.mSelfUrl = mSelfUrl;
    }

    public String getReasonTitle(int i){
        return mReasonDetails.get(i).getTitle();
    }

    public String getReasonId(int i){
        return mReasonDetails.get(i).getId();
    }

    public boolean getReasonValue(int i){
        return mReasonDetails.get(i).isValue();
    }

    public void setReasonDetail(String sId, String sTitle){
        ReasonDetails reasonDetails = new ReasonDetails();
        reasonDetails.setId(sId);
        reasonDetails.setTitle(sTitle);

        mReasonDetails.add(reasonDetails);
    }

    public void setReasonValue(int i, boolean value){
        mReasonDetails.get(i).setValue(value);
    }

    public ArrayList<ReasonDetails> getReasonDetails()
    {
        return mReasonDetails;
    }

    public class ReasonDetails {
        private String mId;
        private String mTitle;
        private boolean mValue;

        public String getId() {
            return mId;
        }

        public void setId(String mId) {
            this.mId = mId;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public boolean isValue() {
            return mValue;
        }

        public void setValue(boolean mValue) {
            this.mValue = mValue;
        }
    }
}
