package com.example.appsynth.paymehost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by jeeraphan on 12/2/16.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mPlayerIDEditText, mReceiverIDEdittext, mAmountEditText;
    private Button mGetTokenButton;
    private Button mLaunchGameButton;
    private String mNameString;

    private static final String EXTRA_NAME = "extra_name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mPlayerIDEditText = (EditText) findViewById(R.id.playerId_editText);
        mReceiverIDEdittext = (EditText) findViewById(R.id.receiverId_editText);
        mAmountEditText = (EditText) findViewById(R.id.amount_editText);
        mGetTokenButton = (Button) findViewById(R.id.get_token_button);
        mLaunchGameButton = (Button) findViewById(R.id.launch_game_button);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mNameString = bundle.getString(EXTRA_NAME);
            mPlayerIDEditText.setText(mNameString);
        }

        mGetTokenButton.setOnClickListener(this);
        mLaunchGameButton.setOnClickListener(this);
    }

    private boolean isVeriryGetToken(){

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
                //GameStartActivity.openActivity(HomeActivity.this);
                break;
        }
    }

    private void getGameToken(){
        if (!isVeriryGetToken()){
            return;
        }

        hideKeyboard();

        String receiverID = mReceiverIDEdittext.getText().toString();
        String amount = mAmountEditText.getText().toString();

//        HSBCGame.requestForGameToken(HomeActivity.this, HGLanguage.ENGLISH, receiverID, Integer.parseInt(amount), new HGRequestGameInterface() {
//            @Override
//            public void onSuccess() {
//            }
//
//            @Override
//            public void onFail(HGError error) {
//            }
//
//            @Override
//            public void onFailHasPendingPayment(HGError error, List<HGTransaction> transactionList) {
//            }
//        });
    }
}
