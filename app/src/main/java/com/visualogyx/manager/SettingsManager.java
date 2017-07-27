package com.visualogyx.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.visualogyx.apirequest.account.AccountSuccessResponseModel;

public class SettingsManager {

    private final String MyPREFERENCES = "VISUALOGYX";
    private final String TypeCamera = "TypeCamera";
    private final String FlashMode = "FlashMode";
    private final String isRememberMe = "isRememberMe";
    private final String RegisterUser = "RegisterUser";
    private Gson gson = new Gson();

    private static SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private static SettingsManager instance = null;

    private SettingsManager(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public static SettingsManager getInstance(Context context) {
        if (instance == null)
            instance = new SettingsManager(context);
        return instance;
    }

    public void setTypeCamera(int value) {
        editor.putInt(TypeCamera, value);
        editor.commit();
    }

    public int getTypeCamera() {
        return sharedpreferences.getInt(TypeCamera, 0);
    }

    public void setFlashMode(String value) {
        editor.putString(FlashMode, value);
        editor.commit();
    }

    public String getFlashMode() {
        return sharedpreferences.getString(FlashMode, "on");
    }

    public void setIsRememberMe(boolean value) {
        editor.putBoolean(isRememberMe, value);
        editor.commit();
    }

    public boolean isRememberMe() {
        return sharedpreferences.getBoolean(isRememberMe, false);
    }

    public void setRegisterUser(AccountSuccessResponseModel value) {
        String json = gson.toJson(value);
        editor.putString(RegisterUser, json);
        editor.commit();
    }

    public AccountSuccessResponseModel getRegisterUser() {
        String json = sharedpreferences.getString(RegisterUser, "");
        AccountSuccessResponseModel accountSuccessResponseModel = gson.fromJson(json, AccountSuccessResponseModel.class);
        return accountSuccessResponseModel;
    }
}
