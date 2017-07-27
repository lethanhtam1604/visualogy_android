package com.visualogyx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.testfairy.TestFairy;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.ErrorResponseModel;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.account.login.LoginByEmailApiRequest;
import com.visualogyx.apirequest.account.login.LoginByPhoneNumberApiRequest;
import com.visualogyx.apirequest.account.login.LoginModel;
import com.visualogyx.manager.Global;
import com.visualogyx.manager.SettingsManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    init();
                    if (Global.settingsManager.isRememberMe()) {
                        if (!Utils.isNetworkConnected(getApplicationContext())) {
                            Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                            intent.putExtra("errorLogin", "Internet not available!");
                            startActivity(intent);
                            finish();
                        } else {
                            AccountSuccessResponseModel accountSuccessResponseModel = Global.settingsManager.getRegisterUser();
                            ApiRequest request;
                            if (accountSuccessResponseModel.typeLogin == 1) {
                                LoginModel loginModel = new LoginModel(accountSuccessResponseModel.email, accountSuccessResponseModel.password);
                                request = new LoginByEmailApiRequest(SplashScreenActivity.this, new LoginRequestResponse(), loginModel);
                            } else {
                                LoginModel loginModel = new LoginModel(accountSuccessResponseModel.phone_number, accountSuccessResponseModel.password);
                                request = new LoginByPhoneNumberApiRequest(SplashScreenActivity.this, new LoginRequestResponse(), loginModel);
                            }
                            request.execute();
                        }
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                }
            }
        };
        timerThread.start();
    }

    public class LoginRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if (status) {
                if (responseObject instanceof AccountSuccessResponseModel) {
                    AccountSuccessResponseModel accountSuccessResponseModelOld = Global.settingsManager.getRegisterUser();
                    AccountSuccessResponseModel accountSuccessResponseModel = (AccountSuccessResponseModel) responseObject;
                    accountSuccessResponseModel.typeLogin = accountSuccessResponseModelOld.typeLogin;
                    accountSuccessResponseModel.password = accountSuccessResponseModelOld.password;
                    Global.settingsManager.setRegisterUser(accountSuccessResponseModel);
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ErrorResponseModel errorResponseModel = (ErrorResponseModel) responseObject;
                    Global.settingsManager.setIsRememberMe(false);
                    Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                    intent.putExtra("errorLogin", errorResponseModel.error_description);
                    startActivity(intent);
                    finish();
                }
            } else {
                Global.settingsManager.setIsRememberMe(false);
                Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                intent.putExtra("errorLogin", responseObject.toString());
                startActivity(intent);
                finish();
            }
        }
    }

    private void init() {
        Global.settingsManager = SettingsManager.getInstance(getApplicationContext());
    }
}
