package com.visualogyx.apirequest.account.login;

public class LoginModel {

    public String emailOrPhoneNumber;
    public String password;

    public LoginModel(String emailOrPhoneNumber, String password) {
        this.emailOrPhoneNumber = emailOrPhoneNumber;
        this.password = password;
    }
}
