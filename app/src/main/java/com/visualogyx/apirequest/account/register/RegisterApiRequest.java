package com.visualogyx.apirequest.account.register;

import android.content.Context;

import com.google.gson.Gson;
import com.visualogyx.apirequest.ApiRequest;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;

public class RegisterApiRequest extends ApiRequest {

    private Gson gson;
    private RegisterModel registerModel;

    public RegisterApiRequest(Context context, RequestResponse callback, RegisterModel registerModel) {
        super(context, callback);
        gson = new Gson();
        this.registerModel = registerModel;
    }

    public String getLoadingMessage() {
        return "";
    }

    public String getRequestUrl() {
        return "/register_user?user_name=" + registerModel.name + "&email=" + registerModel.email
                + "&phone_number=" + registerModel.phoneNumber + "&password=" + registerModel.password;
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
        if (responseSuccess)
            return gson.fromJson(response, AccountSuccessResponseModel.class);
        else
            return response;
    }
}

