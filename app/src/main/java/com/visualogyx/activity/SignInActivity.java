package com.visualogyx.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.testfairy.TestFairy;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.ErrorResponseModel;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.account.login.LoginByEmailApiRequest;
import com.visualogyx.apirequest.account.login.LoginByPhoneNumberApiRequest;
import com.visualogyx.apirequest.account.login.LoginModel;
import com.visualogyx.apirequest.inspection.InspectionSuccessResponseModel;
import com.visualogyx.manager.Global;
import com.visualogyx.manager.ViewExtras;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class SignInActivity extends AppCompatActivity {

    private EditText emailOrPhoneNumberEditText, passwordEditText;
    private TextView errorTV;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            ArrayList<String> requestList = new ArrayList<>();

            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(Manifest.permission.INTERNET);
            }

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(Manifest.permission.CAMERA);
            }

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(Manifest.permission.RECORD_AUDIO);
            }

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (requestList.size() > 0) {
                String[] requestArr = new String[requestList.size()];
                requestArr = requestList.toArray(requestArr);
                ActivityCompat.requestPermissions(this, requestArr, 1);
            } else {
                begin();
            }
        } else {
            begin();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean isGratedFully = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isGratedFully = false;
                break;
            }
        }

        if (isGratedFully) {
            begin();
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.app_name)
                    .titleColor(ViewExtras.getColor(this, R.color.colorMain))
                    .content("Can not grant permission fully!")
                    .positiveText("Exit")
                    .negativeColor(ViewExtras.getColor(this, R.color.main_color))
                    .positiveColor(ViewExtras.getColor(this, R.color.main_color))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.cancel();
                            SignInActivity.this.finish();
                        }
                    })
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .show();
        }
    }

    private void begin() {
        Fabric.with(this, new Crashlytics());
        createActionBar();
        initView();
    }

    private void initView() {
        emailOrPhoneNumberEditText = (EditText) findViewById(R.id.emailOrPhoneNumberEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);
        errorTV = (TextView) findViewById(R.id.errorTV);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        } else {
            String errorLogin = extras.getString("errorLogin");
            errorTV.setVisibility(View.VISIBLE);
            errorTV.setText(errorLogin);
        }
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sign_in);
        setSupportActionBar(toolbar);

        TextView titleBar = (TextView) toolbar.findViewById(R.id.title_text);
        titleBar.setText("Sign In");
        titleBar.setTypeface(null, Typeface.BOLD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    private int typeLogin;

    public void signInBtnClicked(View view) {
        String error = "";
        typeLogin = 1;

        if (!Utils.isEmailValid(emailOrPhoneNumberEditText.getText().toString())) {
            if (!Utils.isMobileValid(emailOrPhoneNumberEditText.getText().toString())) {
                emailOrPhoneNumberEditText.requestFocus();
                emailOrPhoneNumberEditText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                error = "Invalid email or phone number";
            } else
                typeLogin = 2;
        }

        if (error == "" && passwordEditText.getText().toString().compareTo("") == 0) {
            passwordEditText.requestFocus();
            passwordEditText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            error = "Invalid password";
        }

        if (error.compareTo("") != 0) {
            errorTV.setVisibility(View.VISIBLE);
            errorTV.setText(error);
        } else {
            errorTV.setVisibility(View.GONE);

            if(!Utils.isNetworkConnected(this)) {
                Utils.showDisconnectNetworkMessage(this, "Internet not available!");
                return;
            }

            Utils.showHubNoTitle(this);
            LoginModel loginModel = new LoginModel(emailOrPhoneNumberEditText.getText().toString(), passwordEditText.getText().toString());
            ApiRequest request;
            if (typeLogin == 1)
                request = new LoginByEmailApiRequest(this, new LoginRequestResponse(), loginModel);
            else
                request = new LoginByPhoneNumberApiRequest(this, new LoginRequestResponse(), loginModel);
            request.execute();
        }
    }

    public class LoginRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if (status) {
                if (responseObject instanceof AccountSuccessResponseModel) {
                    AccountSuccessResponseModel accountSuccessResponseModel = (AccountSuccessResponseModel) responseObject;
                    Global.settingsManager.setIsRememberMe(rememberMeCheckBox.isChecked());
                    accountSuccessResponseModel.typeLogin = typeLogin;
                    accountSuccessResponseModel.password = passwordEditText.getText().toString();
                    Global.settingsManager.setRegisterUser(accountSuccessResponseModel);
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    SignInActivity.this.finish();
                } else {
                    ErrorResponseModel errorResponseModel = (ErrorResponseModel) responseObject;
                    errorTV.setText(errorResponseModel.error_description);
                    errorTV.setVisibility(View.VISIBLE);
                }
            } else {
                errorTV.setText(responseObject.toString());
                errorTV.setVisibility(View.VISIBLE);
            }
            Utils.hideHub();
        }
    }

    public void newUserClicked(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void forgotPasswordClicked(View view) {
        Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
