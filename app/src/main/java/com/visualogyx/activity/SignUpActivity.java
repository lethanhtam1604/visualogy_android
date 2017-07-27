package com.visualogyx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.testfairy.TestFairy;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;
import com.visualogyx.apirequest.account.register.RegisterApiRequest;
import com.visualogyx.apirequest.account.register.RegisterModel;
import com.visualogyx.manager.Global;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneNumberEditText, passwordEditText, confirmPasswordEditText;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TestFairy.begin(this, getResources().getString(R.string.test_fairy_id));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        createActionBar();
        initView();
    }

    private void initView() {
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);

        errorTV = (TextView) findViewById(R.id.errorTV);
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sign_up);
        setSupportActionBar(toolbar);

        TextView titleBar = (TextView) toolbar.findViewById(R.id.title_text);
        titleBar.setText("Sign Up");
        titleBar.setTypeface(null, Typeface.BOLD);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void signUpBtnClicked(View view) {
        String error = "";
        if (nameEditText.getText().toString().compareTo("") == 0) {
            nameEditText.requestFocus();
            nameEditText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            error = "Invalid name";
        } else if (!Utils.isEmailValid(emailEditText.getText().toString())) {
            emailEditText.requestFocus();
            emailEditText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            error = "Invalid email address";
        } else if (!Utils.isMobileValid(phoneNumberEditText.getText().toString())) {
            phoneNumberEditText.requestFocus();
            phoneNumberEditText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            error = "Invalid phone number";
        } else if (passwordEditText.getText().toString().compareTo("") == 0) {
            passwordEditText.requestFocus();
            passwordEditText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            error = "Invalid password";
        } else if (confirmPasswordEditText.getText().toString().compareTo(passwordEditText.getText().toString()) != 0) {
            confirmPasswordEditText.requestFocus();
            confirmPasswordEditText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            error = "Incorrect password or confirm password";
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
            RegisterModel registerModel = new RegisterModel(nameEditText.getText().toString(), emailEditText.getText().toString(), phoneNumberEditText.getText().toString(), passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString());
            ApiRequest request = new RegisterApiRequest(this, new RegisterUserRequestResponse(), registerModel);
            request.execute();
        }
    }

    public class RegisterUserRequestResponse implements ApiRequest.RequestResponse {
        @Override
        public void requestComplete(boolean status, Object responseObject) {
            if (status) {
                AccountSuccessResponseModel accountSuccessResponseModel = (AccountSuccessResponseModel) responseObject;
                Global.settingsManager.setIsRememberMe(false);
                Global.settingsManager.setRegisterUser(accountSuccessResponseModel);
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                SignUpActivity.this.finish();

            } else {
                errorTV.setText(responseObject.toString());
                errorTV.setVisibility(View.VISIBLE);
            }
            Utils.hideHub();
        }
    }

    public void loginClicked(View view) {
        this.finish();
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
