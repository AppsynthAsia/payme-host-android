package net.appsynth.paymehostdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.appsynth.paymehostdemo.utils.DialogUtils;

import java.util.List;

import playbasis.hsbcsdk.android.models.HGTransaction;
import playbasis.hsbcsdk.android.sdk.HGLanguage;
import playbasis.hsbcsdk.android.sdk.HSBCGame;
import playbasis.hsbcsdk.android.sdk.errors.HGError;
import playbasis.hsbcsdk.android.sdk.interfaces.HGGameFinishInterface;
import playbasis.hsbcsdk.android.sdk.interfaces.HGLaunchGameInterface;
import playbasis.hsbcsdk.android.sdk.interfaces.HGNumberInterface;
import playbasis.hsbcsdk.android.sdk.interfaces.HGRequestPendingInterface;
import playbasis.hsbcsdk.android.sdk.interfaces.HGRequestTokenInterface;


/**
 * Created by jeeraphan on 12/2/16.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mPlayerIDEditText, mReceiverIDEdittext, mAmountEditText;
    private Button mGetTokenButton;
    private Button mLaunchGameButton;
    private Button mShowPendingTransactionButton;
    private String mNameString;
    private TextView mPendingTextView;
    private TextView mSpinTextView;

    private Menu mMenu;
    private HGLanguage mCurrentLanguage = HGLanguage.ENGLISH;

    private static final String EXTRA_NAME = "extra_name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPlayerToken();
        getPendingTransaction();
    }
/*
        Uncomment below to turn on change language menu.
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_language, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        mMenu.findItem(R.id.action_current_language).setTitle(item.getTitle());

        switch (item.getItemId()) {
            case R.id.action_en:
                mCurrentLanguage = HGLanguage.ENGLISH;
                return true;
            case R.id.action_zhs:
                mCurrentLanguage = HGLanguage.CHINESE_SIMPLIFIED;
                return true;
            case R.id.action_zht:
                mCurrentLanguage = HGLanguage.CHINESE_TRADITIONAL;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        mPlayerIDEditText = (EditText) findViewById(R.id.playerId_editText);
        mReceiverIDEdittext = (EditText) findViewById(R.id.receiverId_editText);
        mAmountEditText = (EditText) findViewById(R.id.amount_editText);
        mGetTokenButton = (Button) findViewById(R.id.get_token_button);
        mLaunchGameButton = (Button) findViewById(R.id.launch_game_button);
        mShowPendingTransactionButton = (Button) findViewById(R.id.show_pending_transaction_button);
        mPendingTextView = (TextView) findViewById(R.id.home_pendingTransaction_textView);
        mSpinTextView = (TextView) findViewById(R.id.home_spin_textView);

        mGetTokenButton.setOnClickListener(this);
        mLaunchGameButton.setOnClickListener(this);
        mShowPendingTransactionButton.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mNameString = bundle.getString(EXTRA_NAME);
            mPlayerIDEditText.setText(mNameString);
        }
    }

    private boolean isVerifyGetToken(){

        String playerID = mPlayerIDEditText.getText().toString();
        String receiverID = mReceiverIDEdittext.getText().toString();
        String amount = mAmountEditText.getText().toString();

        if (playerID.isEmpty() || receiverID.isEmpty() || amount.isEmpty()){
            return false;
        }

        return true;
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getGameToken(){
        if (!isVerifyGetToken()){
            DialogUtils.showOKialog(this, getString(R.string.verify_get_token_fail));
            return;
        }

        hideKeyboard();

        String receiverID = mReceiverIDEdittext.getText().toString();
        String amount = mAmountEditText.getText().toString();

        mGetTokenButton.setVisibility(View.INVISIBLE);

        HSBCGame.requestForGameToken(HomeActivity.this, mCurrentLanguage, receiverID, Integer.parseInt(amount), new HGRequestTokenInterface() {
            @Override
            public void onSuccess() {
                mGetTokenButton.setVisibility(View.VISIBLE);
                getPlayerToken();
            }

            @Override
            public void onFail(HGError error) {
                mGetTokenButton.setVisibility(View.VISIBLE);
                //DialogUtils.showOKialog(HomeActivity.this, "requestForGameToken onFail: "+error.getErrorCode());
            }

            @Override
            public void onFailHasPendingPayment(HGError error, List<HGTransaction> transactionList) {
                mGetTokenButton.setVisibility(View.VISIBLE);
                String msg = "Host app handle error code: " + error.getErrorCode();
                DialogUtils.showHostAppDialog(HomeActivity.this, msg);
                getPlayerToken();
            }
        });
    }

    private void getPlayerToken() {

        mSpinTextView.setText(getResources().getString(R.string.loading));

        HSBCGame.numberOfGameToken(new HGNumberInterface() {
            @Override
            public void onSuccess(int numberOfToken) {
                mSpinTextView.setText(String.valueOf(numberOfToken));
            }

            @Override
            public void onFail(HGError error) {
                //DialogUtils.showHostAppDialog(HomeActivity.this, "numberOfGameToken onFail: "+error.getErrorCode());
            }
        });
    }

    private void getPendingTransaction() {

        mPendingTextView.setText(getResources().getString(R.string.loading));

        HSBCGame.getPendingTransactions(new HGRequestPendingInterface() {
            @Override
            public void onSuccess(List<HGTransaction> pendingTransaction) {
                mPendingTextView.setText(String.valueOf(pendingTransaction.size()));
            }

            @Override
            public void onFail(HGError error) {
                //DialogUtils.showHostAppDialog(HomeActivity.this, "Error code: "+error.getErrorCode());
            }
        });

    }


    private void openPendingTransaction() {

        /**
         *

         HSBCGame.presentPopup(HGLanguage.CHINESE_SIMPLIFIED, this, new HGDialogInterface() {
            @Override
            public void onSuccess() {
                Log.d("nut", "onSuccess");
            }
        });


        HSBCGame.campaignOfAvailable(new HGCampaignListInterface() {
            @Override
            public void onSuccess(List<HGCampaign> campaign) {
                Log.d("nut", ""+ campaign.size());
            }


            @Override
            public void onFail(HGError error) {

            }
        });


        HSBCGame.getCurrentLiveCampaign(new HGCampaignInterface() {
            @Override
            public void onSuccess(HGCampaign campaign) {
                Log.d("nut", ""+ campaign.getDateEnd());

            }

            @Override
            public void onFail(HGError error) {

            }
        });


        HSBCGame.getRemainingMoneyInPool(new HGNumberInterface() {
            @Override
            public void onSuccess(int value) {
                Log.d("nut", ""+ value);

            }

            @Override
            public void onFail(HGError error) {

            }
        });



        HSBCGame.getRemainingTokenInPool(new HGNumberInterface() {
            @Override
            public void onSuccess(int value) {
                Log.d("nut", ""+ value);

            }

            @Override
            public void onFail(HGError error) {

            }
        });

        *
        **/


        PendingTransactionActivity.openActivity(this);

    }

    private void openGame() {

        int num = 0;

        try {
            num = Integer.parseInt(mSpinTextView.getText().toString());
        }
        catch (Exception e) {

        }

        if (num <= 0) {
            return;
        }

        HSBCGame.presentGameActivity(this, HGLanguage.CHINESE_SIMPLIFIED, new HGLaunchGameInterface() {

            @Override
            public void onFailHasPendingPayment(HGError error, List<HGTransaction> transactionList) {
                String msg = "Host app handle error code: " + error.getErrorCode();
                DialogUtils.showHostAppDialog(HomeActivity.this, msg);
            }

            @Override
            public void onFail(HGError error) {
                //DialogUtils.showHostAppDialog(HomeActivity.this, "presentGameActivity onFail: " + error.getErrorCode());
            }

        }, new HGGameFinishInterface() {
            @Override
            public void onSuccess(List<HGTransaction> newTransactionList) {
                //DialogUtils.showHostAppDialog(HomeActivity.this, "pendingTransaction : " + newTransactionList.size());
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.get_token_button:
                getGameToken();
                break;
            case R.id.launch_game_button:
                openGame();
                break;
            case R.id.show_pending_transaction_button:
                openPendingTransaction();
                break;
        }
    }


    public static void openActivity(Activity pActivity, String name) {
        Intent intent = new Intent(pActivity, HomeActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        pActivity.startActivity(intent);
        pActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
