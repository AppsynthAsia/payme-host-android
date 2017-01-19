package net.appsynth.paymehostdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.appsynth.paymehostdemo.utils.DialogUtils;

import playbasis.hsbcsdk.android.sdk.HSBCEnvironment;
import playbasis.hsbcsdk.android.sdk.HSBCGame;
import playbasis.hsbcsdk.android.sdk.errors.HGError;
import playbasis.hsbcsdk.android.sdk.interfaces.HGSuccessInterface;


/**
 * Created by jeeraphan on 12/1/16.
 */

public class LoginActivity extends AppCompatActivity {

    private final String API_KEY = "1657828538";
    private final String SECRET_KEY = "24a3673ef9a2567a7ef05fff78074140";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText nameEditText = (EditText) findViewById(R.id.name_edittext);
        Button loginButton = (Button) findViewById(R.id.login_button);

        HSBCGame.init(this, API_KEY, SECRET_KEY, HSBCEnvironment.PROD);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = nameEditText.getText().toString();

                if(name.isEmpty()){
                    return;
                }

                HSBCGame.setPlayerId(name, new HGSuccessInterface() {
                    @Override
                    public void onSuccess() {
                        HomeActivity.openActivity(LoginActivity.this, name);
                    }

                    @Override
                    public void onFail(HGError error) {
                        DialogUtils.showHostAppDialog(LoginActivity.this, "Can't set player id");
                    }
                });
            }
        });

    }

}
