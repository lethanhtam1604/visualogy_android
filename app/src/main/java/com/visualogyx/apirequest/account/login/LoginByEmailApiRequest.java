package com.visualogyx.apirequest.account.login;

import android.content.Context;

import com.google.gson.Gson;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.ErrorResponseModel;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;

public class LoginByEmailApiRequest extends ApiRequest {

    private Gson gson;
    private LoginModel loginModel;

    public LoginByEmailApiRequest(Context context, RequestResponse callback, LoginModel loginModel) {
        super(context, callback);
        gson = new Gson();
        this.loginModel = loginModel;
    }

    public String getLoadingMessage() {
        return "";
    }

    public String getRequestUrl() {
        return "/login?email=" + loginModel.emailOrPhoneNumber + "&password=" + loginModel.password;
    }

    public String getContentType() {
        return "application/json";
    }

    public String getRequestType() {
        return "GET";
    }

    public String parseBody() {
        return null;
    }

    public boolean isOpenProgressBar() {
        return false;
    }

    public boolean isImageProcessing() {
        return false;
    }

    public Object resultReceiver(String response) {
        if (responseSuccess) {
            AccountSuccessResponseModel object = gson.fromJson(response, AccountSuccessResponseModel.class);
            if (object.email == null) {
                responseSuccess = false;
                return gson.fromJson(response, ErrorResponseModel.class);
            }
            return object;
        } else
            return response;
    }
}