package net.appsynth.paymehost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.appsynth.android.hsbcpaymewheel.activities.GameStartActivity;
import net.appsynth.android.hsbcpaymewheel.constants.HGLanguage;
import net.appsynth.android.hsbcpaymewheel.models.HGTransaction;
import net.appsynth.android.hsbcpaymewheel.sdk.HSBCGame;
import net.appsynth.android.hsbcpaymewheel.sdk.errors.HGError;
import net.appsynth.android.hsbcpaymewheel.sdk.interfaces.HGGameFinishInterface;
import net.appsynth.android.hsbcpaymewheel.sdk.interfaces.HGLaunchGameInterface;
import net.appsynth.android.hsbcpaymewheel.sdk.interfaces.HGNumberGameTokenInterface;
import net.appsynth.android.hsbcpaymewheel.sdk.interfaces.HGRequestGameInterface;
import net.appsynth.android.hsbcpaymewheel.utils.DialogUtils;

import java.util.List;

import okhttp3.internal.Util;


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

    private static final String EXTRA_NAME = "extra_name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
        
        getPlayerToken();
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

    public static void openActivity(Activity pActivity, String name) {
        Intent intent = new Intent(pActivity, HomeActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        pActivity.startActivity(intent);
        pActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
                GameStartActivity.openActivity(HomeActivity.this);
                break;
        }
    }

    private void getGameToken(){
        if (!isVerifyGetToken()){
            return;
        }

        hideKeyboard();

        String receiverID = mReceiverIDEdittext.getText().toString();
        String amount = mAmountEditText.getText().toString();

        HSBCGame.requestForGameToken(HomeActivity.this, HGLanguage.ENGLISH, receiverID, Integer.parseInt(amount), new HGRequestGameInterface() {
            @Override
            public void onSuccess() {
                getPlayerToken();
            }

            @Override
            public void onFail(HGError error) {

                DialogUtils.showOKialog(HomeActivity.this, "requestForGameToken onFail: "+error.getErrorCode());

            }

            @Override
            public void onFailHasPendingPayment(HGError error, List<HGTransaction> transactionList) {

                DialogUtils.showOKialog(HomeActivity.this, "onFailHasPendingPayment: "+error.getErrorCode() + " pendingNum : " + transactionList.size());

            }
        });
    }

    public void getPlayerToken() {

        HSBCGame.numberOfGameToken(new HGNumberGameTokenInterface() {
            @Override
            public void onSuccess(int numberOfToken) {
                mSpinTextView.setText(String.valueOf(numberOfToken));
            }

            @Override
            public void onFail(HGError error) {

                DialogUtils.showOKialog(HomeActivity.this, "numberOfGameToken onFail: "+error.getErrorCode());

            }
        });
    }

    public void openGame() {

        HSBCGame.presentGameActivity(this, HGLanguage.ENGLISH, new HGLaunchGameInterface() {
            @Override
            public void onOpenGameSuccess(int playerTokenAmount) {

            }

            @Override
            public void onFailHasPendingPayment(HGError error, List<HGTransaction> transactionList) {

            }

            @Override
            public void onFail(HGError error) {

            }
        }, new HGGameFinishInterface() {
            @Override
            public void onSuccess(List<HGTransaction> pendingTransaction) {

            }

            @Override
            public void onFail(HGError error) {

            }
        });

    }
}
